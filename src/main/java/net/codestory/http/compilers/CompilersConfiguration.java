/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface CompilersConfiguration {
  // Compilers

  void registerCompiler(Supplier<Compiler> compilerFactory, String compiledExtension, String sourceExtension);

  // Handlebars

  void configureHandlebars(Consumer<Handlebars> action);

  void addHandlebarsResolver(ValueResolver resolver);

  default void addHandlebarsHelpers(Object helperSource) {
    configureHandlebars(handlebars -> handlebars.registerHelpers(helperSource));
  }

  default void addHandlebarsHelpers(Class<?> helperSource) {
    configureHandlebars(handlebars -> handlebars.registerHelpers(helperSource));
  }
}