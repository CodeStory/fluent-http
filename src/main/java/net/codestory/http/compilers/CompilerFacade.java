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
package net.codestory.http.compilers;

import static net.codestory.http.io.Strings.extension;
import static net.codestory.http.io.Strings.replaceLast;
import static net.codestory.http.misc.MemoizingSupplier.memoize;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.*;
import net.codestory.http.templating.*;

public class CompilerFacade implements CompilersConfiguration {
  protected final Resources resources;
  protected final Supplier<Compilers> compilers;
  protected final Supplier<ViewCompiler> viewCompiler;

  public CompilerFacade(Env env, Resources resources) {
    this.resources = resources;
    this.compilers = memoize(() -> new Compilers(env, resources));
    this.viewCompiler = memoize(() -> new ViewCompiler(env, resources, this));
  }

  // Configuration

  @Override
  public void registerCompiler(Supplier<Compiler> compilerFactory, String compiledExtension, String sourceExtension) {
    compilers.get().register(compilerFactory, compiledExtension, sourceExtension);
  }

  @Override
  public void configureHandlebars(Consumer<Handlebars> action) {
    viewCompiler.get().configureHandlebars(action);
  }

  @Override
  public void addHandlebarsResolver(ValueResolver resolver) {
    viewCompiler.get().addHandlebarsResolver(resolver);
  }

  // Compilation

  public boolean canCompile(String extension) {
    return compilers.get().canCompile(extension);
  }

  public Set<String> extensionsThatCompileTo(String extension) {
    return compilers.get().extensionsThatCompileTo(extension);
  }

  public String compiledExtension(String extension) {
    return compilers.get().compiledExtension(extension);
  }

  public CacheEntry compile(Path path) throws IOException {
    return compilers.get().compile(resources.sourceFile(path));
  }

  public CacheEntry compile(SourceFile sourceFile) throws IOException {
    return compilers.get().compile(sourceFile);
  }

  public String renderView(String uri, Map<String, ?> variables) {
    return viewCompiler.get().render(uri, variables);
  }

  public Path findPublicSourceFor(String uri) {
    String extension = extension(uri);

    for (String sourceExtension : extensionsThatCompileTo(extension)) {
      Path sourcePath = Paths.get(replaceLast(uri, extension, sourceExtension));

      if (resources.isPublic(sourcePath)) {
        return sourcePath;
      }
    }

    return null;
  }
}
