/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import static java.util.Arrays.*;
import static net.codestory.http.Configuration.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.errors.*;
import net.codestory.http.internal.*;
import net.codestory.http.logs.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.routes.*;
import net.codestory.http.ssl.*;

import javax.net.ssl.*;

public abstract class AbstractWebServer<T extends AbstractWebServer<T>> {
  protected final Env env;

  protected HttpServerWrapper server;
  protected RoutesProvider routesProvider;
  protected int port;

  protected AbstractWebServer() {
    this.env = createEnv();
  }

  protected abstract HttpServerWrapper createHttpServer(Handler handler) throws Exception;

  public T configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
      ? RoutesProvider.fixed(env, configuration)
      : RoutesProvider.reloading(env, configuration);
    return (T) this;
  }

  public T configure(Class<? extends Configuration> configuration) {
    return configure(new ConfigurationReloadingProxy(configuration));
  }

  public T startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(30000);
        return start(port);
      } catch (IllegalStateException e) {
        if (!e.getMessage().contains("Port already in use")) {
          Logs.unableToBindServer(e);
        }
      } catch (Exception e) {
        Logs.unableToBindServer(e);
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
      Logs.mode(env.prodMode());

      server.start(this.port, context, authReq);

      Logs.started(this.port);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      if ((e instanceof BindException) || (e.getCause() instanceof BindException)) {
        throw new IllegalStateException("Port already in use " + this.port);
      }
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
    // We need to make sure that these two lines cannot fail
    // Otherwise no response is made to the client
    //
    RouteCollection routes = routesProvider.get();
    PayloadWriter payloadWriter = routes.createPayloadWriter(request, response);

    try {
      Context context = routes.createContext(request, response);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      handleServerError(payloadWriter, e);
    }
  }

  protected void handleServerError(PayloadWriter payloadWriter, Exception e) {
    try {
      if (e instanceof CompilerException) {
        Logs.compilerError(e);
      } else if (!(e instanceof HttpException) && !(e instanceof NoSuchElementException)) {
        e.printStackTrace();
      }

      Payload errorPage = errorPage(e).withHeader("reason", e.getMessage());
      payloadWriter.writeAndClose(errorPage);
    } catch (IOException error) {
      Logs.unableToServerErrorPage(error);
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
}
