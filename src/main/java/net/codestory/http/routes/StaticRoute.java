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
import net.codestory.http.filters.Filter;
import net.codestory.http.io.*;

import com.sun.net.httpserver.*;

class StaticRoute implements Filter {
  private final String root;

  StaticRoute(String root) {
    if (!root.startsWith("classpath:") && !new File(root).exists()) {
      throw new IllegalArgumentException("Invalid directory for static content: " + root);
    }
    this.root = root;
  }

  @Override
  public boolean apply(String uri, HttpExchange exchange) throws IOException {
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      return false;
    }

    if (uri.endsWith("/")) {
      return apply(uri + "index", exchange);
    }

    return serve(Paths.get(root + uri), exchange)
        || serve(Paths.get(root + uri + ".html"), exchange)
        || serve(Paths.get(root + uri + ".md"), exchange);
  }

  private boolean serve(Path path, HttpExchange exchange) throws IOException {
    if (path.normalize().toString().startsWith(root) && Resources.exists(path)) {
      new Payload(path).writeTo(exchange);
      return true;
    }

    return false;
  }
}
