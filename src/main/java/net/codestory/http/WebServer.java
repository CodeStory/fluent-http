package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import net.codestory.http.payload.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private final HttpServer server;
  private final Map<String, Route> routes;

  public WebServer() {
    try {
      server = HttpServer.create();
      routes = new HashMap<>();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().toString();

    Route route = routes.get(uri);
    if (route != null) {
      Payload payload = new PayloadConverter().convert(route.body());

      byte[] data = payload.data;
      exchange.getResponseHeaders().add("Content-Type", payload.contentType);
      exchange.sendResponseHeaders(200, data.length);
      exchange.getResponseBody().write(data);
    } else {
      exchange.sendResponseHeaders(404, 0);
    }

    exchange.close();
  }

  public void get(String path, Route route) {
    routes.put(path, route);
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

  public static interface Route<Object> {
    public Object body();
  }
}
