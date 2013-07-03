package net.codestory.http.routes;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import net.codestory.http.*;

import com.sun.net.httpserver.*;

class StaticRoute implements RouteHolder {
  private static final String WELCOME_FILE = "index.html";

  private final String root;

  StaticRoute(String fromUrl) {
    URL rootResource = getClass().getClassLoader().getResource(fromUrl);
    if (rootResource == null) {
      throw new IllegalArgumentException("Invalid path for static content");
    }
    this.root = Paths.get(rootResource.getFile()).normalize().toString();
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
