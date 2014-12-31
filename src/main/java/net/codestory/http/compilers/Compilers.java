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

import net.codestory.http.compilers.markdown.MarkdownCompiler;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;

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
  private final Map<String, String> compiledExtension = new HashMap<>();
  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
  private final DiskCache diskCache;

  public Compilers(Env env, Resources resources) {
    boolean prodMode = env.prodMode();

    diskCache = new DiskCache("V5", prodMode);
    register(() -> new CoffeeCompiler(prodMode), ".js", ".coffee", ".litcoffee");
    register(() -> new CoffeeSourceMapCompiler(), ".map", ".coffee.map", ".litcoffee.map"); // ?
    register(() -> new MarkdownCompiler(), ".html", ".md", ".markdown");
    register(() -> new LessCompiler(resources, prodMode), ".css", ".less");
  }

  public void register(Supplier<Compiler> compilerFactory, String targetExtension, String firstExtension, String... moreExtensions) {
    Supplier<Compiler> compilerLazyFactory = memoize(compilerFactory);

    Set<String> uncompiledExtensions = extensionsThatCompileTo.computeIfAbsent(targetExtension, k -> new HashSet<>());

    compilerByExtension.put(firstExtension, compilerLazyFactory);
    compiledExtension.put(firstExtension, targetExtension);
    uncompiledExtensions.add(firstExtension);

    for (String extension : moreExtensions) {
      compilerByExtension.put(extension, compilerLazyFactory);
      compiledExtension.put(extension, targetExtension);
      uncompiledExtensions.add(extension);
    }
  }

  public boolean canCompile(String extension) {
    return compilerByExtension.containsKey(extension);
  }

  public Set<String> extensionsThatCompileTo(String extension) {
    return extensionsThatCompileTo.getOrDefault(extension, emptySet());
  }

  public String compiledExtension(String extension) {
    return compiledExtension.get(extension);
  }

  public CacheEntry compile(SourceFile sourceFile) {
    return cache.computeIfAbsent(sourceFile.getFileName() + ';' + sourceFile.getSource(), ignore -> doCompile(sourceFile));
  }

  private CacheEntry doCompile(SourceFile sourceFile) {
    for (Entry<String, Supplier<Compiler>> entry : compilerByExtension.entrySet()) {
      String extension = entry.getKey();

      if (sourceFile.hasExtension(extension)) {
        // Hack until I find something better
        if (extension.equals(".less")) { // TODO: handle ".less.source"
          if (sourceFile.getSource().contains("@import")) {
            return CacheEntry.noCache(doCompile(sourceFile, entry.getValue()));
          }
        }

        String sha1 = sourceFile.sha1();
        return diskCache.computeIfAbsent(sha1, extension, () -> doCompile(sourceFile, entry.getValue()));
      }
    }

    return CacheEntry.fromString(sourceFile.getSource());
  }

  private String doCompile(SourceFile sourcefile, Supplier<Compiler> compilerSupplier) {
    return compilerSupplier.get().compile(sourcefile);
  }
}
