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
import static net.codestory.http.io.Resources.isPublic;
import static net.codestory.http.io.Strings.*;

import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.*;

public class SourceRoute implements Route {
  private static final Path NOT_FOUND = Paths.get("");

  private final CompilerFacade compilers;

  SourceRoute(CompilerFacade compilers) {
    this.compilers = compilers;
  }

  @Override
  public boolean matchUri(String uri) {
    return uri.endsWith(".source") && isPublic(findPath(uri, getSourcePath(uri)));
  }

  @Override
  public boolean matchMethod(String method) {
    return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
  }

  @Override
  public Object body(Context context) {
    String sourceUri = getSourcePath(context.uri());
    return findPath(context.uri(), sourceUri);
  }

  public Path findPath(String uri, String sourceUri) {
    Path sourcePath = Resources.findExistingPath(sourceUri);
    if ((sourcePath != null) && isPublic(sourcePath)) {
      return sourcePath;
    }

    String extension = extension(sourceUri);
    for (String sourceExtension : compilers.extensionsThatCompileTo(extension)) {
      sourcePath = Paths.get(getSourcePath(replaceLast(uri, extension, sourceExtension)));

      if (isPublic(sourcePath)) {
        return sourcePath;
      }
    }

    return NOT_FOUND;
  }

  private static String getSourcePath(String uri) {
    return Strings.substringBeforeLast(uri, ".source");
  }
}
