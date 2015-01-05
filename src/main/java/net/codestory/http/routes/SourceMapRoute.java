/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.io.Strings.substringBeforeLast;

import java.io.IOException;
import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.payload.Payload;

class SourceMapRoute implements Route {
  private final Resources resources;
  private final CompilerFacade compilerFacade;

  SourceMapRoute(Resources resources, CompilerFacade compilerFacade) {
    this.resources = resources;
    this.compilerFacade = compilerFacade;
  }

  @Override
  public boolean matchUri(String uri) {
    return (uri.endsWith(".coffee.map") || uri.endsWith(".litcoffee.map")) && resources.isPublic(pathSource(uri));
  }

  @Override
  public boolean matchMethod(String method) {
    return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
  }

  @Override
  public Payload body(Context context) throws IOException {
    String uri = context.uri();

    Path mapPath = Paths.get(uri);
    Path sourcePath = pathSource(uri);

    String compile = compilerFacade.compile(new SourceFile(mapPath, resources.read(sourcePath, UTF_8))).content();
    return new Payload("text/plain;charset=UTF-8", compile); // Temp
  }

  private static Path pathSource(String uri) {
    return Paths.get(substringBeforeLast(uri, ".map"));
  }
}
