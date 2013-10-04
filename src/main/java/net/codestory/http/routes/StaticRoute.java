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

import static net.codestory.http.routes.Match.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.io.*;

import com.sun.net.httpserver.*;

class StaticRoute implements Route {
  private static final String[] EXTENSIONS = {"", ".html", ".md", ".markdown"};

  @Override
  public Match apply(String uri, HttpExchange exchange) throws IOException {
    if (uri.endsWith("/")) {
      return apply(uri + "index", exchange);
    }

    for (String extension : EXTENSIONS) {
      Match match = serve(Paths.get(uri + extension), exchange);
      if (WRONG_URL != match) {
        return match;
      }
    }

    return WRONG_URL;
  }

  private Match serve(Path path, HttpExchange exchange) throws IOException {
    for (Path part : path) {
      if (part.toString().equals("..") || part.toString().startsWith("_")) {
        return WRONG_URL;
      }
    }

    if (!Resources.exists(path)) {
      return WRONG_URL;
    }

    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      return WRONG_METHOD;
    }

    new Payload(path).writeTo(exchange);
    return OK;
  }
}
