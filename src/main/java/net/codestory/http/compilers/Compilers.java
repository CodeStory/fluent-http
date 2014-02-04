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
package net.codestory.http.compilers;

import static net.codestory.http.misc.MemoizingSupplier.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public enum Compilers {
  INSTANCE;

  private final Map<String, Supplier<? extends Compiler>> compilerByExtension = new HashMap<>();
  private final ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

  private Compilers() {
    register(CoffeeCompiler::new, ".coffee", ".litcoffee");
    register(MarkdownCompiler::new, ".md", ".markdown");
    register(LessCompiler::new, ".less");
    register(LessSourceMapCompiler::new, ".css.map");
    register(AsciidocCompiler::new, ".asciidoc", ".adoc");
  }

  public void register(Supplier<? extends Compiler> compilerFactory, String firstExtension, String... moreExtensions) {
    Supplier<? extends Compiler> compilerLazyFactory = memoize(compilerFactory);

    compilerByExtension.put(firstExtension, compilerLazyFactory);
    for (String extension : moreExtensions) {
      compilerByExtension.put(extension, compilerLazyFactory);
    }
  }

  public String compile(Path path, String content) {
    return cache.computeIfAbsent(path.toString() + ";" + content, (ignore) -> doCompile(path, content));
  }

  private String doCompile(Path path, String content) {
    String filename = path.toString();

    for (Map.Entry<String, Supplier<? extends Compiler>> entry : compilerByExtension.entrySet()) {
      String extension = entry.getKey();
      if (filename.endsWith(extension)) {
        try {
          Compiler compiler = entry.getValue().get();
          return compiler.compile(path, content);
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    return content;
  }
}
