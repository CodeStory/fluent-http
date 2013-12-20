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
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.ssl.*;

import org.simpleframework.http.*;
import org.simpleframework.http.core.*;
import org.simpleframework.transport.*;
import org.simpleframework.transport.connect.*;

import javax.net.ssl.*;

public class WebServer {
  private final Server server;
  private final SocketConnection connection;
  private RoutesProvider routesProvider;
  private int port;

  public WebServer(Configuration configuration) {
    this();
    configure(configuration);
  }

  public WebServer() {
    try {
      server = new ContainerServer(this::handle);
      connection = new SocketConnection(server);
      reset();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  public static void main(String[] args) throws Exception {
    new WebServer().configure(routes -> {
      routes.filter(new LogRequestFilter());
    }).start(8080);
  }

  public WebServer configure(Configuration configuration) {
    routesProvider = devMode()
        ? RoutesProvider.reloading(configuration)
        : RoutesProvider.fixed(configuration);
    return this;
  }

  public WebServer startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 20; i++) {
      try {
        int port = 8183 + random.nextInt(1000);
        start(port);
        return this;
      } catch (Exception e) {
        System.err.println("Unable to bind server: " + e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  private static int overrideWithCloudbeesPort(int port) {
    return Integer.parseInt(System.getProperty("app.port", "" + port));
  }

  public WebServer start(int port) {
    return startWithContext(port, null);
  }

  public WebServer startSSL(int port, Path pathCertificate, Path pathPrivateKey) {
    SSLContext context;
    try {
      context = new SSLContextFactory().create(pathCertificate, pathPrivateKey);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read certificate or key", e);
    }
    return startWithContext(port, context);
  }

  private WebServer startWithContext(int port, SSLContext context) {
    try {
      this.port = overrideWithCloudbeesPort(port);

      connection.connect(new InetSocketAddress(this.port), context);

      System.out.println("Server started on port " + this.port);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to bind the web server on port " + this.port, e);
    }

    return this;
  }

  public int port() {
    return port;
  }

  public void reset() {
    routesProvider = RoutesProvider.empty();
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
      applyRoutes(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      try {
        Payload errorPage = onError(e);
        errorPage.writeTo(response);
      } catch (IOException error) {
        System.out.println("Unable to serve an error page " + error);
        error.printStackTrace();
      }
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  protected void applyRoutes(Request request, Response response) throws IOException {
    Payload payload = routesProvider.get().apply(request, response);
    if (payload.isError()) {
      payload = onError(payload);
    }
    payload.writeTo(response);
  }

  protected Payload onError(Payload payload) {
    return onError(payload, null);
  }

  protected Payload onError(Exception e) {
    int code = (e instanceof HttpException) ? ((HttpException) e).code() : 500;
    return onError(new Payload(code), e);
  }

  protected Payload onError(Payload payload, Exception e) {
    Exception shownError = devMode() ? e : null;
    return new ErrorPage(payload, shownError).payload();
  }

  protected boolean devMode() {
    return !Boolean.getBoolean("PROD_MODE");
  }
}
