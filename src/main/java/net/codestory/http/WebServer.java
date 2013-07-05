package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import net.codestory.http.dev.*;
import net.codestory.http.errors.*;
import net.codestory.http.routes.*;

import com.sun.net.httpserver.*;

public class WebServer {
  private final HttpServer server;
  private final DevMode devMode = new DevMode();
  private final RouteCollection routes = new RouteCollection(devMode);

  public WebServer() {
    try {
      server = HttpServer.create();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  public WebServer configure(Configuration configuration) {
    devMode.setLastConfiguration(configuration);
    configuration.configure(routes);
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
      server.bind(new InetSocketAddress(port), 0);
      server.createContext("/", this::onRequest);
      server.start();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to bind the web server on port " + port);
    }
    return this;
  }

  public int port() {
    return server.getAddress().getPort();
  }

  public void stop() {
    server.stop(0);
  }

  protected void onRequest(HttpExchange exchange) {
    try {
      applyRoutes(exchange);
    } catch (Exception e) {
      onError(exchange, e);
    } finally {
      exchange.close();
    }
  }

  protected void applyRoutes(HttpExchange exchange) throws IOException {
    if (!routes.apply(exchange)) {
      onPageNotFound(exchange);
    }
  }

  protected void onError(HttpExchange exchange, Exception e) {
    if (devMode.isDevMode()) {
      new ErrorPage(500, e).writeTo(exchange);
    } else {
      new ErrorPage(500).writeTo(exchange);
    }
  }

  protected void onPageNotFound(HttpExchange exchange) throws IOException {
    new ErrorPage(404).writeTo(exchange);
  }
}
