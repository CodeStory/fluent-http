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

import java.io.*;
import java.nio.file.*;

class AsciidocCompiler implements Compiler {
  private final NashornCompiler nashornCompiler = new NashornCompiler(
      "asciidoc/opal.js",
      "asciidoc/asciidoctor.js",
      "asciidoc/render.js");

  @Override
  public String compile(Path path, String source) throws IOException {
    return nashornCompiler.compile(source);
  }
}