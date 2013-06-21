package net.codestory.http;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private HttpServer server;

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().toString();

    switch (uri) {
      case "/": {
        byte[] body = "Hello World".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        break;
      }
      default: {
        exchange.sendResponseHeaders(404, 0);
      }
    }

    exchange.close();
  }

  public void start(int port) throws IOException {
    server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", this);
    server.start();
  }

  public void stop() {
    server.stop(0);
  }
}
