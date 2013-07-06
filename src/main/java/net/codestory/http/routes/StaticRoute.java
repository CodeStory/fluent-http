/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
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
    if (file.normalize().startsWith(root) && file.toFile().exists()) {
      new Payload(file).writeTo(exchange);
      return true;
    }

    return false;
  }
}
