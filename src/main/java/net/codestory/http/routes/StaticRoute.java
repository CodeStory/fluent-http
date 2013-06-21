package net.codestory.http.routes;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import net.codestory.http.*;

import com.sun.net.httpserver.*;

class StaticRoute implements RouteHolder {
  private final Path root;

  StaticRoute(String fromUrl) {
    URL rootResource = getClass().getClassLoader().getResource(fromUrl);
    if (rootResource == null) {
      throw new IllegalArgumentException("Invalid path for static content");
    }
    this.root = Paths.get(rootResource.getFile()).normalize();
  }

  @Override
  public boolean apply(String uri, HttpExchange exchange) throws IOException {
    Path file = Paths.get(root.toString(), uri).normalize();
    if (!file.startsWith(root)) {
      return false;
    }

    new Payload(file).writeTo(exchange);
    return true;
  }
}
