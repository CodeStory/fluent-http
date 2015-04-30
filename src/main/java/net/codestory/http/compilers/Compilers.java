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

import static java.util.Collections.emptySet;
import static java.util.Map.Entry;
import static net.codestory.http.misc.MemoizingSupplier.memoize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import net.codestory.http.misc.Sha1;

public class Compilers {
  private final Env env;
  private final Supplier<DiskCache> diskCache;
  private final Map<String, Supplier<Compiler>> compilerByExtension = new HashMap<>();
  private final Map<String, Set<String>> extensionsThatCompileTo = new HashMap<>();
  private final Map<String, String> compiledExtensions = new HashMap<>();
  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

  public Compilers(Env env, Resources resources) {
    this.env = env;

    boolean prodMode = env.prodMode();
    diskCache = memoize(() -> new DiskCache("V11", prodMode));
    register(() -> new LessCompiler(resources, prodMode), ".css", ".less");
    register(() -> new CoffeeCompiler(prodMode), ".js", ".coffee");
    register(() -> new CoffeeCompiler(prodMode), ".js", ".litcoffee");
    if (!prodMode) {
      register(() -> new CoffeeSourceMapCompiler(), ".coffee.map", ".coffee.map");
      register(() -> new CoffeeSourceMapCompiler(), ".litcoffee.map", ".litcoffee.map");
    }
  }

  public void register(Supplier<Compiler> compilerFactory, String compiledExtension, String sourceExtension) {
    Supplier<Compiler> compilerLazyFactory = memoize(compilerFactory);

    compilerByExtension.put(sourceExtension, compilerLazyFactory);
    compiledExtensions.put(sourceExtension, compiledExtension);
    extensionsThatCompileTo.computeIfAbsent(compiledExtension, k -> new HashSet<>()).add(sourceExtension);
  }

  public boolean canCompile(String extension) {
    return compilerByExtension.containsKey(extension);
  }

  public Set<String> extensionsThatCompileTo(String extension) {
    return extensionsThatCompileTo.getOrDefault(extension, emptySet());
  }

  public String compiledExtension(String extension) {
    return compiledExtensions.get(extension);
  }

  public CacheEntry compile(SourceFile sourceFile) {
    String key = sourceFile.getFileName() + ';' + sourceFile.getSource();

    return cache.computeIfAbsent(key, ignore -> doCompile(sourceFile, key));
  }

  private CacheEntry doCompile(SourceFile sourceFile, String key) {
    for (Entry<String, Supplier<Compiler>> entry : compilerByExtension.entrySet()) {
      String extension = entry.getKey();
      if (!sourceFile.hasExtension(extension)) {
        continue;
      }

      Supplier<Compiler> compiler = entry.getValue();

      // Hack until I find something better
      if (".less".equals(extension) && sourceFile.getSource().contains("@import")) {
        return CacheEntry.noCache(compiler.get().compile(sourceFile));
      }

      if (env.diskCache()) {
        String sha1 = Sha1.of(key);
        return diskCache.get().computeIfAbsent(sha1, extension, () -> compiler.get().compile(sourceFile));
      } else {
        return CacheEntry.fromString(compiler.get().compile(sourceFile));
      }
    }

    throw new IllegalArgumentException("Unable to compile " + sourceFile.getFileName() + ". Unknown extension");
  }
}
