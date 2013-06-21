package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private final HttpServer server;
  private final Map<String, Route> routes;
  private final Map<String, OneParamRoute> oneParamRoutes;

  public WebServer() {
    try {
      server = HttpServer.create();
      routes = new HashMap<>();
      oneParamRoutes = new HashMap<>();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      String uri = exchange.getRequestURI().toString();

      Route route = routes.get(uri);
      if (route != null) {
        new Payload(route.body()).writeTo(exchange);
        return;
      }

      OneParamRoute oneParamRoute = oneParamRoutes.get("/hello/${name}");
      if (oneParamRoute != null) {
        new Payload(oneParamRoute.body("Dave")).writeTo(exchange);
        return;
      }

      exchange.sendResponseHeaders(404, 0);
    } finally {
      exchange.close();
    }
  }

  public void get(String path, Route route) {
    routes.put(path, route);
  }

  public void get(String path, OneParamRoute route) {
    oneParamRoutes.put(path, route);
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

  public static interface OneParamRoute<Object> {
    public Object body(String param);
  }
}
