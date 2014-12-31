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

import static net.codestory.http.constants.Methods.*;

import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.io.*;

class SourceRoute implements Route {
  private final Resources resources;

  SourceRoute(Resources resources) {
    this.resources = resources;
  }

  @Override
  public boolean matchUri(String uri) {
    return uri.endsWith(".source") && resources.isPublic(getSourcePath(uri));
  }

  @Override
  public boolean matchMethod(String method) {
    return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
  }

  @Override
  public Path body(Context context) {
    return getSourcePath(context.uri());
  }

  private static Path getSourcePath(String uri) {
    return Paths.get(Strings.substringBeforeLast(uri, ".source"));
  }
}
