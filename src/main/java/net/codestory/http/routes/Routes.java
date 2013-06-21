package net.codestory.http.routes;

import java.io.*;
import java.util.*;

import net.codestory.http.*;

import com.sun.net.httpserver.*;

public class Routes {
  private final List<RouteWrapper> routes = new ArrayList<>();

  public boolean apply(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().toString();

    for (RouteWrapper route : routes) {
      if (route.apply(uri, exchange)) {
        return true;
      }
    }

    return false;
  }

  public void get(String uriPattern, Route route) {
    add(uriPattern, route);
  }

  public void get(String uriPattern, OneParamRoute route) {
    add(uriPattern, route);
  }

  public void get(String uriPattern, TwoParamsRoute route) {
    add(uriPattern, route);
  }

  private void add(String uriPattern, AnyRoute route) {
    routes.add(new RouteWrapper(uriPattern, route));
  }

  private static class RouteWrapper {
    final UriParser uriParser;
    final AnyRoute route;

    RouteWrapper(String uriPattern, AnyRoute route) {
      this.uriParser = new UriParser(uriPattern);
      this.route = route;
    }

    public boolean apply(String uri, HttpExchange exchange) throws IOException {
      if (uriParser.matches(uri)) {
        new Payload(route.body(uriParser.params(uri))).writeTo(exchange);
        return true;
      }
      return false;
    }
  }
}
