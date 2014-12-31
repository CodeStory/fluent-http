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

import net.codestory.http.Context;
import net.codestory.http.compilers.CompiledPath;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Cache;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.*;

import static net.codestory.http.constants.Methods.GET;
import static net.codestory.http.constants.Methods.HEAD;
import static net.codestory.http.io.Strings.extension;
import static net.codestory.http.io.Strings.replaceLast;

class StaticRoute implements Route {
  private static final Path NOT_FOUND = Paths.get("");

  private final Resources resources;
  private final CompilerFacade compilers;
  private final Function<String, Object> findPath;

  StaticRoute(boolean cached, Resources resources, CompilerFacade compilers) {
    if (cached) {
      this.findPath = new Cache<>(this::findPath);
    } else {
      this.findPath = this::findPath;
    }
    this.resources = resources;
    this.compilers = compilers;
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

  private Object findPath(String uri) {
    Path path = resources.findExistingPath(uri);
    if ((path != null) && resources.isPublic(path)) {
      if (compilers.canCompile(extension(uri))) {
        return new CompiledPath(path, path);
      }
      return path;
    }

    return findCompilableTo(uri);
  }

  private Object findCompilableTo(String uri) {
    String extension = extension(uri);

    for (String sourceExtension : compilers.extensionsThatCompileTo(extension)) {
      Path path = Paths.get(replaceLast(uri, extension, sourceExtension));

      if (resources.isPublic(path)) {
        return new CompiledPath(path, path);
      }
    }

    return NOT_FOUND;
  }
}
