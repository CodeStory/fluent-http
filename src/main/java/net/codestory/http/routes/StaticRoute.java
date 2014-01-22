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

import java.nio.file.*;
import java.util.concurrent.*;

import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;

class StaticRoute implements Route {
  private final ConcurrentMap<String, Path> pathForUri = new ConcurrentHashMap<>(10);
  private static final Path NOT_FOUND = Paths.get("");

  @Override
  public boolean matchUri(String uri) {
    Path result;
    if (Env.INSTANCE.devMode()) {
      result = findPath(uri);
    } else {
      result = pathForUri.computeIfAbsent(uri, StaticRoute::findPath);
    }
    return result != NOT_FOUND;
  }

  @Override
  public boolean matchMethod(String method) {
    return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
  }

  @Override
  public Object body(Context context) {
    String uri = context.uri();
    if (Env.INSTANCE.devMode()) {
      return findPath(uri);
    }
    return pathForUri.computeIfAbsent(uri, StaticRoute::findPath);
  }

  private static Path findPath(String uri) {
    Path path = Resources.findExistingPath(uri);
    return (path != null) && Resources.isPublic(path) ? path : NOT_FOUND;
  }
}
