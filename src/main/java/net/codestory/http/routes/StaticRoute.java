package net.codestory.http.routes;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.*;

import com.sun.net.httpserver.*;

class StaticRoute implements RouteHolder {
  private static final String WELCOME_FILE = "index.html";

  private final String root;

  StaticRoute(Path path) {
    this.root = path.normalize().toString();
  }

  static StaticRoute forUrl(String url) {
    if (url.startsWith("classpath:")) {
      return new StaticClasspathRoute(url.substring(10));
    }
    if (url.startsWith("file:")) {
      return new StaticFileRoute(url.substring(5));
    }

    throw new IllegalArgumentException("Invalid path for static content. Should be prefixed by file: or classpath:");
  }

  @Override
  public boolean apply(String uri, HttpExchange exchange) throws IOException {
    if (uri.equals("/")) {
      uri = WELCOME_FILE;
    }

    Path file = Paths.get(root, uri);

    return serve(file, exchange) || serve(Paths.get(file + ".html"), exchange);
  }

  private boolean serve(Path file, HttpExchange exchange) throws IOException {
    if (!file.normalize().startsWith(root) || !file.toFile().exists()) {
      return false;
    }

    new Payload(file).writeTo(exchange);
    return true;
  }
}
