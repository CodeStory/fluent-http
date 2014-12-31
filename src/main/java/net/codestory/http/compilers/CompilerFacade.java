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

import static net.codestory.http.misc.MemoizingSupplier.memoize;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.*;
import net.codestory.http.templating.*;

public class CompilerFacade implements CompilersConfiguration {
  protected final Supplier<Compilers> compilers;
  protected final Supplier<HandlebarsCompiler> handlebars;
  protected final Supplier<ViewCompiler> viewCompiler;

  public CompilerFacade(Env env, Resources resources) {
    this.compilers = memoize(() -> new Compilers(env, resources));
    this.handlebars = memoize(() -> new HandlebarsCompiler(resources, this));
    this.viewCompiler = memoize(() -> new ViewCompiler(resources, this));
  }

  // Configuration

  @Override
  public void registerCompiler(Supplier<Compiler> compilerFactory, String targetExtension, String firstExtension, String... moreExtensions) {
    compilers.get().register(compilerFactory, targetExtension, firstExtension, moreExtensions);
  }

  @Override
  public void configureHandlebars(Consumer<Handlebars> action) {
    handlebars.get().configure(action);
  }

  @Override
  public void addHandlebarsResolver(ValueResolver resolver) {
    handlebars.get().addResolver(resolver);
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

  public CacheEntry compile(SourceFile sourceFile) throws IOException {
    return compilers.get().compile(sourceFile);
  }

  public String handlebar(String template, Map<String, ?> variables) throws IOException {
    return handlebars.get().compile(template, variables);
  }

  public String renderView(String uri, Map<String, ?> variables) {
    return viewCompiler.get().render(uri, variables);
  }
}
