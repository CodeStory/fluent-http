package net.codestory.http.routes;

import java.io.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class RouteCollection implements Routes {
  private final List<RouteHolder> routes = new ArrayList<>();

  @Override
  public void serve(String fromUrl) {
    routes.add(new StaticRoute(fromUrl));
  }

  @Override
  public void get(String uriPattern, Route route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, OneParamRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, TwoParamsRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsRoute route) {
    add(uriPattern, route);
  }

  private void add(String uriPattern, AnyRoute route) {
    routes.add(new RouteWrapper(uriPattern, route));
  }

  public boolean apply(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().toString();

    for (RouteHolder route : routes) {
      if (route.apply(uri, exchange)) {
        return true;
      }
    }

    return false;
  }
}
