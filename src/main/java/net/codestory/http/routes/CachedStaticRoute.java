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

import java.nio.file.*;
import java.util.concurrent.*;

import net.codestory.http.io.*;

class CachedStaticRoute extends StaticRoute {
  private final ConcurrentHashMap<String, Path> pathForUri = new ConcurrentHashMap<>(10);

  @Override
  protected Path path(String uri) {
    return pathForUri.computeIfAbsent(uri, CachedStaticRoute::findPath);
  }

  private static Path findPath(String uri) {
    Path path = Resources.findExistingPath(uri);
    return (path != null) && Resources.isPublic(path) ? path : NOT_FOUND;
  }
}
