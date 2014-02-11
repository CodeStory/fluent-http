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

import static java.util.Map.*;
import static net.codestory.http.misc.MemoizingSupplier.*;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public enum Compilers {
  INSTANCE;

  private final Map<String, Supplier<Compiler>> compilerByExtension = new HashMap<>();
  private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
  private final DiskCache diskCache = new DiskCache("V1");

  private Compilers() {
    register(CoffeeCompiler::new, ".coffee", ".litcoffee");
    register(MarkdownCompiler::new, ".md", ".markdown");
    register(LessCompiler::new, ".less");
    register(LessSourceMapCompiler::new, ".css.map");
    register(AsciidocCompiler::new, ".asciidoc", ".adoc");
  }

  public void register(Supplier<Compiler> compilerFactory, String firstExtension, String... moreExtensions) {
    Supplier<Compiler> compilerLazyFactory = memoize(compilerFactory);

    compilerByExtension.put(firstExtension, compilerLazyFactory);
    for (String extension : moreExtensions) {
      compilerByExtension.put(extension, compilerLazyFactory);
    }
  }

  public CacheEntry compile(Path path, String content) {
    return cache.computeIfAbsent(path.toString() + ";" + content, ignore -> doCompile(path, content));
  }

  private CacheEntry doCompile(Path path, String content) {
    for (Entry<String, Supplier<Compiler>> entry : compilerByExtension.entrySet()) {
      String extension = entry.getKey();

      if (path.toString().endsWith(extension)) {
        return diskCache.computeIfAbsent(path, content, entry.getValue(), extension);
      }
    }

    return CacheEntry.memory(content);
  }
}
