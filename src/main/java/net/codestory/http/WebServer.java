package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class WebServer implements HttpHandler {
  private final HttpServer server;
  private final List<RouteWrapper> routes;

  public WebServer() {
    try {
      server = HttpServer.create();
      routes = new ArrayList<>();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().toString();

    try {
      for (RouteWrapper route : routes) {
        if (route.apply(uri, exchange)) {
          return;
        }
      }

      exchange.sendResponseHeaders(404, 0);
    } finally {
      exchange.close();
    }
  }

  public void get(String uriPattern, Route route) {
    routes.add(new RouteWrapper(uriPattern, route, null, null));
  }

  public void get(String uriPattern, OneParamRoute route) {
    routes.add(new RouteWrapper(uriPattern, null, route, null));
  }

  public void get(String uriPattern, TwoParamsRoute route) {
    routes.add(new RouteWrapper(uriPattern, null, null, route));
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

  private static class RouteWrapper {
    private final UriParser uriParser;
    private final Route route;
    private final OneParamRoute oneParamRoute;
    private final TwoParamsRoute twoParamsRoute;

    private RouteWrapper(String uriPattern, Route route, OneParamRoute oneParamRoute, TwoParamsRoute twoParamsRoute) {
      this.uriParser = new UriParser(uriPattern);
      this.route = route;
      this.oneParamRoute = oneParamRoute;
      this.twoParamsRoute = twoParamsRoute;
    }

    public boolean apply(String uri, HttpExchange exchange) throws IOException {
      if (route != null) {
        if (uriParser.matches(uri)) {
          new Payload(route.body()).writeTo(exchange);
          return true;
        }
      }

      if (oneParamRoute != null) {
        if (uriParser.matches(uri)) {
          String param = uriParser.params(uri)[0];
          new Payload(oneParamRoute.body(param)).writeTo(exchange);
          return true;
        }
      }

      if (twoParamsRoute != null) {
        if (uriParser.matches(uri)) {
          String param1 = uriParser.params(uri)[0];
          String param2 = uriParser.params(uri)[1];
          new Payload(twoParamsRoute.body(param1, param2)).writeTo(exchange);
          return true;
        }
      }

      return false;
    }
  }
}
