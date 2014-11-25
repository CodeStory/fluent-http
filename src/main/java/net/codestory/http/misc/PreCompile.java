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
package net.codestory.http.misc;

import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Strings;
import net.codestory.http.templating.Site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.codestory.http.io.Resources.APP_FOLDER;
import static net.codestory.http.io.Resources.read;
import static net.codestory.http.io.Strings.replaceLast;

public class PreCompile {
  private final Site site;
  private final CompilerFacade compilers;

  public PreCompile(Env env) {
    this.site = new Site(env);
    this.compilers = new CompilerFacade(env);
  }

  public static void main(String[] args) {
    new PreCompile(new Env(true, false, false, false)).run();
  }

  public void run() {
    site.getResourceList().parallelStream().forEach(path -> preCompile(path));
  }

  protected void preCompile(String path) {
    String extension = Strings.extension(path);
    if (!compilers.canCompile(extension)) {
      return;
    }

    Path fromPath = Paths.get(path);
    Path toPath = toPath(path, extension);

    System.out.println("Pre-compile [" + fromPath + "] to [" + toPath + "]");
    try {
      byte[] bytes = compile(fromPath);
      write(bytes, toPath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to pre-compile " + path);
    }
  }

  protected Path toPath(String path, String extension) {
    String compiledExtension = compilers.compiledExtension(extension);

    String newName = replaceLast(path, extension, compiledExtension);

    return Paths.get(APP_FOLDER, newName);
  }

  protected byte[] compile(Path fromPath) throws IOException {
    return compilers.compile(fromPath, read(fromPath, UTF_8)).toBytes();
  }

  protected void write(byte[] bytes, Path toPath) throws IOException {
    Files.createDirectories(toPath.getParent());
    Files.write(toPath, bytes);
  }
}
