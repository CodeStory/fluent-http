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

import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.io.Resources.*;
import static net.codestory.http.io.Strings.*;
import static net.codestory.http.types.ContentTypes.*;

import java.nio.file.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;

class StaticRoute implements Route {
  private static final Path NOT_FOUND = Paths.get("");

  private final Function<String, Object> findPath;

  StaticRoute(boolean cached) {
    if (cached) {
      this.findPath = new Cache<>(StaticRoute::findPath);
    } else {
      this.findPath = StaticRoute::findPath;
    }
  }

  @Override
  public boolean matchUri(String uri) {
    return findPath.apply(uri) != NOT_FOUND;
  }

  @Override
  public boolean matchMethod(String method) {
    return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
  }

  @Override
  public Object body(Context context) {
    String uri = context.uri();

    return findPath.apply(uri);
  }

  private static Object findPath(String uri) {
    Path path = findExistingPath(uri);
    if (pathDoesNotExist(path)) {
      return findFileCompilableToPath(uri);
    }

    return is_binary(path) ? path : new CompiledPath(path, path);
  }

  private static Object findFileCompilableToPath(String uri) {
    if (uri.endsWith(".js")) {
      return findPath(replaceLast(uri, ".js", ".coffee"));
    }
    if (uri.endsWith(".css")) {
      return findPath(replaceLast(uri, ".css", ".less"));
    }
    return NOT_FOUND;
  }

  private static boolean pathDoesNotExist(Path path) {
    return (path == null) || !Resources.isPublic(path);
  }
}
