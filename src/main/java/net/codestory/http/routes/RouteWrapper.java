package net.codestory.http.routes;

import java.io.*;

import net.codestory.http.*;

import com.sun.net.httpserver.*;

class RouteWrapper implements RouteHolder {
  final UriParser uriParser;
  final AnyRoute route;

  RouteWrapper(String uriPattern, AnyRoute route) {
    this.uriParser = new UriParser(uriPattern);
    this.route = route;
  }

  @Override
  public boolean apply(String uri, HttpExchange exchange) throws IOException {
    if (uriParser.matches(uri)) {
      Payload.wrap(route.body(uriParser.params(uri))).writeTo(exchange);
      return true;
    }
    return false;
  }
}
