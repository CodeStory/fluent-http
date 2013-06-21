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
    routes.add(new RouteWrapper(uriPattern, route));
  }

  public void get(String uriPattern, OneParamRoute route) {
    routes.add(new RouteWrapper(uriPattern, route));
  }

  public void get(String uriPattern, TwoParamsRoute route) {
    routes.add(new RouteWrapper(uriPattern, route));
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

  public static interface AnyRoute {
    Object body(String[] params);
  }

  public static interface Route extends AnyRoute {
    Object body();

    @Override
    default Object body(String[] params) {
      return body();
    }
  }

  public static interface OneParamRoute extends AnyRoute {
    Object body(String param);

    @Override
    default Object body(String[] params) {
      return body(params[0]);
    }
  }

  public static interface TwoParamsRoute extends AnyRoute {
    Object body(String param1, String param2);

    @Override
    default Object body(String[] params) {
      return body(params[0], params[1]);
    }
  }

  private static class RouteWrapper {
    private final UriParser uriParser;
    private final AnyRoute route;

    private RouteWrapper(String uriPattern, AnyRoute route) {
      this.uriParser = new UriParser(uriPattern);
      this.route = route;
    }

    public boolean apply(String uri, HttpExchange exchange) throws IOException {
      if (!uriParser.matches(uri)) {
        return false;
      }

      String[] params = uriParser.params(uri);
      Object body = route.body(params);
      Payload payload = new Payload(body);
      payload.writeTo(exchange);

      return true;
    }
  }
}
