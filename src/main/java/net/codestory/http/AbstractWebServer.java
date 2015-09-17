/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import static java.util.Collections.singletonList;
import static net.codestory.http.Configuration.*;

import java.net.*;
import java.nio.file.*;
import java.util.*;

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
  protected static final int PORT_8080 = 8080;
  protected static final int RANDOM_PORT_START_RETRY = 30;

  protected final HttpServerWrapper server;
  protected final Env env;

  protected RoutesProvider routesProvider;
  protected int port = -1;

  protected AbstractWebServer(int count, int select, int websocketThreads) {
    this.server = createHttpServer(this::handleHttp, this::handleWebSocket, count, select, websocketThreads);
    this.env = createEnv();
  }

  protected abstract HttpServerWrapper createHttpServer(Handler httpHandler, WebSocketHandler webSocketHandler,
                                                        int count, int select, int websocketThreads);

  protected Env createEnv() {
    return new Env();
  }

  @SuppressWarnings("unchecked")
  public T configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
        ? RoutesProvider.fixed(env, configuration)
        : RoutesProvider.reloading(env, configuration);
    return (T) this;
  }

  public T startOnRandomPort() {
    for (int i = 0; i < RANDOM_PORT_START_RETRY; i++) {
      try {
        return start(0);
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
    return startSSL(port, singletonList(pathCertificate), pathPrivateKey, null);
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

  @SuppressWarnings("unchecked")
  protected T startWithContext(int port, SSLContext context, boolean authReq) {
    if (routesProvider == null) {
      configure(NO_ROUTE);
    }

    int thePort = (port == 0) ? 0 : env.overriddenPort(port);

    try {
      Logs.mode(env.prodMode());

      this.port = server.start(thePort, context, authReq);

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

  protected void handleHttp(Request request, Response response) {
    // We need to make sure that these two lines cannot fail
    // Otherwise no response is made to the client
    //
    RouteCollection routes = routesProvider.get();

    PayloadWriter writer = routes.createPayloadWriter(request, response);
    try {
      Payload payload = routes.apply(request, response);

      writer.writeAndClose(payload);
    } catch (Exception e) {
      writer.writeErrorPage(e);
    }
  }

  protected void handleWebSocket(WebSocketSession session, Request request, Response response) {
    RouteCollection routes = routesProvider.get();

    try {
      WebSocketListener listener = routes.createWebSocketListener(session, request, response);
      session.register(listener);
    } catch (Exception e) {
      throw new IllegalStateException("WebSocket error", e);
    }
  }

  public void stop() {
    try {
      env.folderWatcher().stop();
      server.stop();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }
}
