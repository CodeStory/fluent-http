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

import net.codestory.http.misc.Env;
import net.codestory.http.misc.Sha1;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.util.Collections.emptySet;
import static java.util.Map.Entry;
import static net.codestory.http.misc.MemoizingSupplier.memoize;

public class Compilers {
  private final Map<String, Supplier<Compiler>> compilerByExtension = new HashMap<>();
  private final Map<String, Set<String>> extensionsThatCompileTo = new HashMap<>();
  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
  private final DiskCache diskCache;

  public Compilers(Env env) {
    boolean prodMode = env.prodMode();

    diskCache = new DiskCache("V3", prodMode);
    register(() -> new CoffeeCompiler(prodMode), ".js", ".coffee", ".litcoffee");
    register(() -> new CoffeeSourceMapCompiler(), ".map", ".coffee.map", ".litcoffee.map"); // ?
    register(() -> new MarkdownCompiler(), ".html", ".md", ".markdown");
    register(() -> new LessCompiler(prodMode), ".css", ".less");
  }

  public void register(Supplier<Compiler> compilerFactory, String targetExtension, String firstExtension, String... moreExtensions) {
    Supplier<Compiler> compilerLazyFactory = memoize(compilerFactory);

    Set<String> uncompiledExtensions = extensionsThatCompileTo.computeIfAbsent(targetExtension, k -> new HashSet<>());

    compilerByExtension.put(firstExtension, compilerLazyFactory);
    uncompiledExtensions.add(firstExtension);

    for (String extension : moreExtensions) {
      compilerByExtension.put(extension, compilerLazyFactory);
      uncompiledExtensions.add(extension);
    }
  }

  public Set<String> extensionsThatCompileTo(String extension) {
    return extensionsThatCompileTo.getOrDefault(extension, emptySet());
  }

  public CacheEntry compile(Path path, String content) {
    return cache.computeIfAbsent(path.toString() + ";" + content, ignore -> doCompile(path, content));
  }

  private CacheEntry doCompile(Path path, String content) {
    for (Entry<String, Supplier<Compiler>> entry : compilerByExtension.entrySet()) {
      String extension = entry.getKey();

      if (path.toString().endsWith(extension)) {
        // Hack until I find something better
        if (extension.equals(".less")) { // TODO: handle ".less.source"
          if (content.contains("@import")) {
            return CacheEntry.noCache(doCompile(path, content, entry.getValue()));
          }
        }

        String sha1 = Sha1.of(content);
        return diskCache.computeIfAbsent(sha1, extension, () -> doCompile(path, content, entry.getValue()));
      }
    }

    return CacheEntry.fromString(content);
  }

  private String doCompile(Path path, String content, Supplier<Compiler> compilerSupplier) {
    Compiler compiler = compilerSupplier.get();
    return compiler.compile(path, content);
  }
}
