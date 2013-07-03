package net.codestory.http.routes;

import java.io.*;
import java.net.*;
import java.nio.file.*;

class StaticFileRoute extends StaticRoute {
  StaticFileRoute(String directoryPath) {
    super(toPath(directoryPath));
  }

  private static Path toPath(String directoryPath) {
    File rootResource = new File(directoryPath);
    if (!rootResource.exists()) {
      throw new IllegalArgumentException("Invalid directory for static content: " + directoryPath);
    }

    return Paths.get(directoryPath);
  }
}
