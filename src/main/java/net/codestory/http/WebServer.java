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

import static java.util.Arrays.*;
import static net.codestory.http.Configuration.NO_ROUTE;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.errors.*;
import net.codestory.http.filters.log.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.routes.*;
import net.codestory.http.ssl.*;
import net.codestory.http.templating.*;

import org.slf4j.*;

public class WebServer {
  private final static Logger LOG = LoggerFactory.getLogger(WebServer.class);

  private final HttpServerWrapper server;
  private Env env;
  private RoutesProvider routesProvider;
  private int port;

  public WebServer() {
    this(NO_ROUTE);
  }

  public WebServer(Class<? extends Configuration> configuration) {
    this(new ConfigurationReloadingProxy(configuration));
  }

  public WebServer(Configuration configuration) {
    try {
      server = new SimpleServerWrapper(this::handle);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
    configure(configuration);
  }

  public static void main(String[] args) throws Exception {
    new WebServer(routes -> routes
      .filter(new LogRequestFilter()))
      .start(8080);
  }

  public WebServer configure(Configuration configuration) {
    env = new Env();
    routesProvider = env.prodMode()
      ? RoutesProvider.fixed(configuration)
      : RoutesProvider.reloading(configuration);
    return this;
  }

  public WebServer startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(10000);
        start(port);
        return this;
      } catch (Exception e) {
        LOG.error("Unable to bind server", e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  public WebServer start() {
    return start(8080);
  }

  public WebServer start(int port) {
    return startWithContext(port, null, false);
  }

  public WebServer startSSL(int port, Path pathCertificate, Path pathPrivateKey) {
    return startSSL(port, asList(pathCertificate), pathPrivateKey, null);
  }

  public WebServer startSSL(int port, List<Path> pathChain, Path pathPrivateKey) {
    return startSSL(port, pathChain, pathPrivateKey, null);
  }

  public WebServer startSSL(int port, List<Path> pathChain, Path pathPrivateKey, List<Path> pathTrustAnchors) {
    SSLContext context;
    try {
      context = new SSLContextFactory().create(pathChain, pathPrivateKey, pathTrustAnchors);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read certificate or key", e);
    }
    boolean authReq = pathTrustAnchors != null;
    return startWithContext(port, context, authReq);
  }

  private WebServer startWithContext(int port, SSLContext context, boolean authReq) {
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

    return this;
  }

  public int port() {
    return port;
  }

  public void reset() {
    configure(NO_ROUTE);
  }

  public void stop() {
    try {
      server.stop();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }

  void handle(Request request, Response response) {
    try {
      RouteCollection routes = routesProvider.get();

      Context context = routes.createContext(request, response);
      PayloadWriter payloadWriter = routes.createPayloadWriter(request, response);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      // Cannot be created by routes since it was not initialized properly
      // TODO: get rid of new Site() here
      //
      PayloadWriter payloadWriter = new PayloadWriter(env, new Site(), request, response);
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

      payloadWriter.writeAndClose(errorPage(e));
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
}
