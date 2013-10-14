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

import static net.codestory.http.routes.Match.*;

import java.io.*;
import java.net.*;
import java.util.*;

import net.codestory.http.errors.*;
import net.codestory.http.routes.*;

import org.simpleframework.http.*;
import org.simpleframework.http.core.*;
import org.simpleframework.transport.*;
import org.simpleframework.transport.connect.*;

public class WebServer {
  private final Server server;
  private final SocketConnection connection;
  private final RouteCollection routes = new RouteCollection();
  private Configuration lastConfiguration;
  private int port;

  public WebServer() {
    try {
      server = new ContainerServer(this::handle);
      connection = new SocketConnection(server);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  public static void main(String[] args) throws Exception {
    new WebServer().start(8080);
  }

  public WebServer configure(Configuration configuration) {
    routes.reset();

    configuration.configure(routes);

    if (devMode()) {
      lastConfiguration = configuration;
    }
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

  public WebServer start(int port) {
    try {
      SocketAddress address = new InetSocketAddress(port);
      connection.connect(address);
      this.port = port;

      System.out.println("Server started on port " + port);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to bind the web server on port " + port, e);
    }
    return this;
  }

  public int port() {
    return port;
  }

  public void reset() {
    lastConfiguration = null;
    routes.reset();
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
      hotReloadConfigurationInDevMode();
      applyRoutes(request, response);
    } catch (Exception e) {
      System.out.println("Error " + e);
      e.printStackTrace();
      try {
        onError(e, response);
      } catch (Exception ioe) {
        System.out.println("Unable to server an error page " + ioe);
        ioe.printStackTrace();
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
    Match match = routes.apply(request, response);
    if (match != OK) {
      onPageNotFound(match, response);
    }
  }

  protected void onPageNotFound(Match match, Response response) throws IOException {
    if (match == WRONG_METHOD) {
      errorPage(405, null).writeTo(response);
    } else {
      errorPage(404, null).writeTo(response);
    }
  }

  protected void onError(Exception e, Response response) throws IOException {
    if (devMode()) {
      errorPage(500, e).writeTo(response);
    } else {
      errorPage(500, null).writeTo(response);
    }
  }

  protected Payload errorPage(int code, Exception e) throws IOException {
    return new ErrorPage(code, e).payload();
  }

  protected boolean devMode() {
    return !Boolean.getBoolean("PROD_MODE");
  }

  protected void hotReloadConfigurationInDevMode() {
    if (lastConfiguration != null) {
      System.out.println("Reloading configuration");
      configure(lastConfiguration);
    }
  }
}
