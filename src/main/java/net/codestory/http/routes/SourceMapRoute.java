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

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.compilers.Compiler;
import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.payload.*;
import net.codestory.http.types.*;

class SourceMapRoute implements Route {
  @Override
  public Payload apply(String uri, Context context) throws IOException {
    if (!uri.endsWith(".css.map")) {
      return Payload.notFound();
    }

    Path pathMap = Paths.get(uri);
    Path pathLess = Paths.get(Strings.substringBeforeLast(uri, ".css.map") + ".less");

    if (!Resources.isPublic(pathLess)) {
      return Payload.notFound();
    }

    if (!"GET".equalsIgnoreCase(context.method())) {
      return Payload.methodNotAllowed();
    }

    String contentType = ContentTypes.get(pathMap);
    String less = Resources.read(pathLess, UTF_8);
    String map = Compiler.compile(pathMap, less);

    return new Payload(contentType, map);
  }
}
