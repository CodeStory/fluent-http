package net.codestory.http;

import java.io.*;
import java.net.*;

import net.codestory.http.routes.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private final HttpServer server;
  private final Routes routes = new Routes();

  public WebServer() {
    try {
      server = HttpServer.create();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  public Routes routes() {
    return routes;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      if (!routes.apply(exchange)) {
        exchange.sendResponseHeaders(404, 0);
      }
    } finally {
      exchange.close();
    }
  }

  public void start(int port) throws IOException {
    server.bind(new InetSocketAddress(port), 0);
    server.createContext("/", this);
    server.start();
  }

  public int port() {
    return server.getAddress().getPort();
  }

  public void stop() {
    server.stop(0);
  }
}
