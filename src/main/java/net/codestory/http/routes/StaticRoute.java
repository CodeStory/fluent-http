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
import java.net.*;
import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

import com.sun.net.httpserver.*;

class StaticRoute implements RouteHolder {
  private static final String WELCOME_FILE = "index.html";

  private final String root;

  StaticRoute(String root) {
    if (!root.startsWith("classpath:") && !new File(root).exists()) {
      throw new IllegalArgumentException("Invalid directory for static content: " + root);
    }
    this.root = root;
  }

  @Override
  public boolean apply(String uri, HttpExchange exchange) throws IOException {
    if (uri.equals("/")) {
      uri = WELCOME_FILE;
    }

    Path file = Paths.get(root, uri);

    return serve(file, exchange) || serve(Paths.get(file + ".html"), exchange);
  }

  private boolean serve(Path path, HttpExchange exchange) throws IOException {
    if (path.normalize().startsWith(root) && Resources.exists(path)) {
      new Payload(path).writeTo(exchange);
      return true;
    }

    return false;
  }
}
