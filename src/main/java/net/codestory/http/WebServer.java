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

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;

import net.codestory.http.errors.*;
import net.codestory.http.filters.log.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.routes.*;
import net.codestory.http.ssl.*;

import org.simpleframework.http.*;
import org.simpleframework.http.core.*;
import org.simpleframework.transport.*;
import org.simpleframework.transport.connect.*;
import org.slf4j.*;

import javax.net.ssl.*;

public class WebServer {
  private final static Logger LOG = LoggerFactory.getLogger(WebServer.class);

  private final Server server;
  private final SocketConnection connection;
  private RoutesProvider routesProvider;
  private int port;

  public WebServer() {
    this(routes -> {
    });
  }

  public WebServer(Configuration configuration) {
    try {
      server = new ContainerServer(this::handle);
      connection = new SocketConnection(server);
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
    routesProvider = Env.INSTANCE.prodMode()
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
    return startWithContext(port, null);
  }

  public WebServer startSSL(int port, Path pathCertificate, Path pathPrivateKey) {
    return startSSL(port, Arrays.asList(pathCertificate), pathPrivateKey);
  }

  public WebServer startSSL(int port, List<Path> pathChain, Path pathPrivateKey) {
    SSLContext context;
    try {
      context = new SSLContextFactory().create(pathChain, pathPrivateKey);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read certificate or key", e);
    }
    return startWithContext(port, context);
  }

  private WebServer startWithContext(int port, SSLContext context) {
    try {
      this.port = Env.INSTANCE.overriddenPort(port);

      connection.connect(new InetSocketAddress(this.port), context);

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
    configure(routes -> {
    });
  }

  public void stop() {
    try {
      server.stop();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }

  void handle(Request request, Response response) {
    Context context = null;

    try {
      RouteCollection routes = routesProvider.get();
      context = new Context(request, response, routes.getIocAdapter());

      applyRoutes(routes, context);
    } catch (Exception e) {
      if (context == null) {
        // Didn't manage to initialize a full context
        // because the routes failed to load
        //
        context = new Context(request, response, null);
      }
      handleServerError(context, e);
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

  protected void applyRoutes(RouteCollection routeCollection, Context context) throws IOException {
    Payload payload = routeCollection.apply(context);
    if (payload.isError()) {
      payload = errorPage(payload);
    }
    payload.writeTo(context);
  }

  protected void handleServerError(Context context, Exception e) {
    if (!(e instanceof HttpException) && !(e instanceof NoSuchElementException)) {
      e.printStackTrace();
    }

    try {
      errorPage(e).writeTo(context);
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
    Exception shownError = Env.INSTANCE.prodMode() ? null : e;
    return new ErrorPage(payload, shownError).payload();
  }
}
