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

import static java.nio.charset.StandardCharsets.*;
import static net.codestory.http.misc.MemoizingSupplier.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.codestory.http.io.*;
import net.codestory.http.misc.*;

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
      if (!filename.endsWith(extension)) {
        continue;
      }

      try {
        String sha1 = Sha1.of(content);

        File file = new File(System.getProperty("user.home"), ".code-story/cache/" + extension.substring(1) + "/" + sha1);
        String fromCache = readFromCache(file);
        if (fromCache != null) {
          return fromCache;
        }

        Compiler compiler = entry.getValue().get();
        String compiled = compiler.compile(path, content);
        writeToCache(file, compiled);

        return compiled;
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }

    return content;
  }

  private static String readFromCache(File file) throws IOException {
    if (!file.exists()) {
      return null;
    }

    try (InputStream input = new FileInputStream(file)) {
      return InputStreams.readString(input, UTF_8);
    }
  }

  private static void writeToCache(File file, String data) throws IOException {
    File parentFile = file.getParentFile();
    if (!parentFile.exists() && !parentFile.mkdirs()) {
      throw new IllegalStateException("Unable to create cache folder: " + parentFile);
    }

    File tmpFile = new File(file.getAbsolutePath()+ ".tmp");
    try (Writer writer = new FileWriter(file)) {
      writer.write(data);
    }
    tmpFile.renameTo(file);
  }
}
