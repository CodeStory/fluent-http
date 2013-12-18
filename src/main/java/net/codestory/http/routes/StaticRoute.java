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

import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.payload.*;

class StaticRoute implements Route {
  protected static final Path NOT_FOUND = Paths.get("");

  @Override
  public Payload apply(String uri, Context context) throws IOException {
    Path path = path(uri);
    if (path == NOT_FOUND) {
      if (!uri.endsWith("/") && (path(uri + "/") != NOT_FOUND)) {
        return Payload.seeOther(uri + "/");
      }
      return Payload.notFound();
    }

    if (!"GET".equalsIgnoreCase(context.method())) {
      return Payload.methodNotAllowed();
    }

    return new Payload(path);
  }

  protected Path path(String uri) {
    Path path = Resources.findExistingPath(uri);
    return (path != null) && Resources.isPublic(path) ? path : NOT_FOUND;
  }
}
