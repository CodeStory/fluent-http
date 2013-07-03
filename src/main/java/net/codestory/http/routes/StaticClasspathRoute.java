package net.codestory.http.routes;

import java.net.*;
import java.nio.file.*;

class StaticClasspathRoute extends StaticRoute {
  StaticClasspathRoute(String fromUrl) {
    super(toPath(fromUrl));
  }

  private static Path toPath(String classpathUrl) {
    URL rootResource = ClassLoader.getSystemClassLoader().getResource(classpathUrl);
    if (rootResource == null) {
      throw new IllegalArgumentException("Invalid classpath for static content: " + classpathUrl);
    }

    return Paths.get(rootResource.getFile());
  }
}
