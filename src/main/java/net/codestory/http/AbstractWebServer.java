/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http;

import static java.util.Arrays.asList;
import static net.codestory.http.Configuration.NO_ROUTE;

import net.codestory.http.compilers.CompilerException;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.compilers.Compilers;
import net.codestory.http.errors.ErrorPage;
import net.codestory.http.errors.HttpException;
import net.codestory.http.internal.*;
import net.codestory.http.misc.Env;
import net.codestory.http.misc.NamedDaemonThreadFactory;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.reload.*;
import net.codestory.http.routes.RouteCollection;
import net.codestory.http.ssl.*;
import net.codestory.http.templating.HandlebarsCompiler;
import net.codestory.http.templating.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.*;

public abstract class AbstractWebServer<T extends AbstractWebServer<T>> {
  protected final static Logger LOG = LoggerFactory.getLogger(AbstractWebServer.class);

  protected final Env env;
  protected final CompilerFacade compilers;
  protected final ExecutorService executorService;

  protected HttpServerWrapper server;
  protected RoutesProvider routesProvider;
  protected int port;

  protected AbstractWebServer() {
    this.env = createEnv();
    this.compilers = createCompilerFacade();
    this.executorService = createExecutorService();
  }

  protected abstract HttpServerWrapper createHttpServer(Handler handler) throws Exception;

  public T configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
      ? RoutesProvider.fixed(env, compilers, configuration)
      : RoutesProvider.reloading(env, compilers, configuration);
    return (T) this;
  }

  public T configure(Class<? extends Configuration> configuration) {
    return configure(new ConfigurationReloadingProxy(configuration));
  }

  public T startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(10000);
        return start(port);
      } catch (Exception e) {
        LOG.error("Unable to bind server", e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  public T start() {
    return start(8080);
  }

  public T start(int port) {
    return startWithContext(port, null, false);
  }

  public T startSSL(int port, Path pathCertificate, Path pathPrivateKey) {
    return startSSL(port, asList(pathCertificate), pathPrivateKey, null);
  }

  public T startSSL(int port, List<Path> pathChain, Path pathPrivateKey) {
    return startSSL(port, pathChain, pathPrivateKey, null);
  }

  public T startSSL(int port, List<Path> pathChain, Path pathPrivateKey, List<Path> pathTrustAnchors) {
    SSLContext context;
    try {
      context = new SSLContextFactory().create(pathChain, pathPrivateKey, pathTrustAnchors);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read certificate or key", e);
    }
    boolean authReq = pathTrustAnchors != null;
    return startWithContext(port, context, authReq);
  }

  protected T startWithContext(int port, SSLContext context, boolean authReq) {
    try {
      server = createHttpServer(this::handle);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to create http server", e);
    }

    if (routesProvider == null) {
      configure(NO_ROUTE);
    }

    this.port = env.overriddenPort(port);

    try {
      LOG.info(env.prodMode() ? "Production mode" : "Dev mode");

      server.start(this.port, context, authReq);

      LOG.info("Server started on port {}", this.port);
    } catch (RuntimeException e) {
      throw e;
    } catch (BindException e) {
      throw new IllegalStateException("Port already in use " + this.port);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to bind the web server on port " + this.port, e);
    }

    return (T) this;
  }

  public int port() {
    return port;
  }

  public void stop() {
    try {
      server.stop();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }

  protected void handle(Request request, Response response) {
    try {
      RouteCollection routes = routesProvider.get();

      Context context = routes.createContext(request, response);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      PayloadWriter payloadWriter = routes.createPayloadWriter(request, response, executorService);
      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      PayloadWriter payloadWriter = new PayloadWriter(request, response, env, new Site(env), compilers, executorService);
      handleServerError(payloadWriter, e);
    }
  }

  protected void handleServerError(PayloadWriter payloadWriter, Exception e) {
    try {
      if (e instanceof CompilerException) {
        LOG.error(e.getMessage());
      } else if (!(e instanceof HttpException) && !(e instanceof NoSuchElementException)) {
        e.printStackTrace();
      }

      Payload errorPage = errorPage(e).withHeader("reason", e.getMessage());
      payloadWriter.writeAndClose(errorPage);
    } catch (IOException error) {
      LOG.warn("Unable to serve an error page", error);
    }
  }

  protected Payload errorPage(Payload payload) {
    return errorPage(payload, null);
  }

  protected Payload errorPage(Exception e) {
    int code = 500;
    if (e instanceof HttpException) {
      code = ((HttpException) e).code();
    } else if (e instanceof NoSuchElementException) {
      code = 404;
    }

    return errorPage(new Payload(code), e);
  }

  protected Payload errorPage(Payload payload, Exception e) {
    Exception shownError = env.prodMode() ? null : e;
    return new ErrorPage(payload, shownError).payload();
  }

  protected Env createEnv() {
    return new Env();
  }

  protected CompilerFacade createCompilerFacade() {
    Compilers compilers = new Compilers(env);
    HandlebarsCompiler handlebar = new HandlebarsCompiler(compilers);

    return new CompilerFacade(compilers, handlebar);
  }

  protected ExecutorService createExecutorService() {
    return Executors.newCachedThreadPool(new NamedDaemonThreadFactory());
  }
}
