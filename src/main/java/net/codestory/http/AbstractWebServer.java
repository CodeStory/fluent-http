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
import static net.codestory.http.constants.HttpStatus.*;

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
import net.codestory.http.websockets.*;

import javax.net.ssl.*;

public abstract class AbstractWebServer<T extends AbstractWebServer<T>> {
  private static final int PORT_8080 = 8080;
  private static final int RANDOM_PORT_START_RETRY = 30;
  private static final int RANDOM_PORTS_LOWER_BOUND = 8183;
  private static final int RANDOM_PORTS_COUNT = 50000;

  protected final Env env;

  protected HttpServerWrapper server;
  protected RoutesProvider routesProvider;
  protected int port = -1;

  protected AbstractWebServer() {
    this.env = createEnv();
  }

  protected abstract HttpServerWrapper createHttpServer(Handler httpHandler, WebSocketHandler webSocketHandler) throws Exception;

  public T configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
        ? RoutesProvider.fixed(env, configuration)
        : RoutesProvider.reloading(env, configuration);
    return (T) this;
  }

  public T startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < RANDOM_PORT_START_RETRY; i++) {
      try {
        int randomPort = RANDOM_PORTS_LOWER_BOUND + random.nextInt(RANDOM_PORTS_COUNT);
        return start(randomPort);
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
    return start(PORT_8080);
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
      server = createHttpServer(this::handleHttp, this::connectWebSocket);
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
    if (port == -1) {
      throw new IllegalStateException("The web server is not started");
    }

    return port;
  }

  public void stop() {
    try {
      env.folderWatcher().stop();
      server.stop();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }

  protected void handleHttp(Request request, Response response) {
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

      payloadWriter.writeAndClose(payload, e -> handleServerError(payloadWriter, e));
    } catch (Exception e) {
      handleServerError(payloadWriter, e);
    }
  }

  protected void connectWebSocket(WebSocketSession session, Request request, Response response) {
    RouteCollection routes = routesProvider.get();

    try {
      Context context = routes.createContext(request, response);

      WebSocketListener listener = routes.createWebSocketListener(session, context);
      session.register(listener);
    } catch (Exception e) {
      throw new IllegalStateException("WebSocket error", e);
    }
  }

  protected void handleServerError(PayloadWriter payloadWriter, Throwable e) {
    try {
      if (e instanceof CompilerException) {
        Logs.compilerError(e);
      } else if (!(e instanceof HttpException) && !(e instanceof NoSuchElementException)) {
        Logs.unexpectedError(e);
      }

      Payload errorPage = errorPage(e).withHeader("reason", e.getMessage());
      payloadWriter.writeAndCloseSync(errorPage);
    } catch (IOException error) {
      Logs.unableToServeErrorPage(error);
    }
  }

  protected Payload errorPage(Payload payload) {
    return errorPage(payload, null);
  }

  protected Payload errorPage(Throwable e) {
    int code = INTERNAL_SERVER_ERROR;
    if (e instanceof HttpException) {
      code = ((HttpException) e).code();
    } else if (e instanceof NoSuchElementException) {
      code = NOT_FOUND;
    }

    return errorPage(new Payload(code), e);
  }

  protected Payload errorPage(Payload payload, Throwable e) {
    Throwable shownError = env.prodMode() ? null : e;
    return new ErrorPage(payload, shownError).payload();
  }

  protected Env createEnv() {
    return new Env();
  }
}
