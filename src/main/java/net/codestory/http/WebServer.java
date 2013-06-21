package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private final HttpServer server;
  private final Map<String, Route> routes;
  private final Map<String, OneParamRoute> oneParamRoutes;
  private final Map<String, TwoParamsRoute> twoParamsRoute;

  public WebServer() {
    try {
      server = HttpServer.create();
      routes = new HashMap<>();
      oneParamRoutes = new HashMap<>();
      twoParamsRoute = new HashMap<>();
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

      for (Map.Entry<String, OneParamRoute> routeEntry : oneParamRoutes.entrySet()) {
        String uriPattern = routeEntry.getKey();
        OneParamRoute oneParamRoute = routeEntry.getValue();

        UriParser uriParser = new UriParser(uriPattern); // TODO : do it once per route

        if (uriParser.matches(uri)) {
          String param = uriParser.params(uri)[0];
          new Payload(oneParamRoute.body(param)).writeTo(exchange);
          return;
        }
      }

      for (Map.Entry<String, TwoParamsRoute> routeEntry : twoParamsRoute.entrySet()) {
        String uriPattern = routeEntry.getKey();
        TwoParamsRoute oneParamRoute = routeEntry.getValue();

        UriParser uriParser = new UriParser(uriPattern); // TODO : do it once per route

        if (uriParser.matches(uri)) {
          String param1 = uriParser.params(uri)[0];
          String param2 = uriParser.params(uri)[1];
          new Payload(oneParamRoute.body(param1, param2)).writeTo(exchange);
          return;
        }
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

  public void get(String path, TwoParamsRoute route) {
    twoParamsRoute.put(path, route);
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

  public static interface Route {
    Object body();
  }

  public static interface OneParamRoute {
    Object body(String param);
  }

  public static interface TwoParamsRoute {
    Object body(String param1, String param2);
  }
}
