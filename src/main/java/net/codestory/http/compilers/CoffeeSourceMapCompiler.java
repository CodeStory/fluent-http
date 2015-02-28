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

import java.util.Map;

import static java.util.Collections.singletonMap;
import static net.codestory.http.io.Strings.replaceLast;

public class CoffeeSourceMapCompiler implements Compiler {
  private final NashornCompiler nashornCompiler = NashornCompiler.get(
    "META-INF/resources/webjars/coffee-script/1.9.1/coffee-script.min.js",
    "coffee-script/toSourceMap.js");

  @Override
  public String compile(SourceFile sourceFile) {
    Map<String, Object> options = singletonMap("__literate", sourceFile.hasExtension(".litcoffee.map"));

    String filename = replaceLast(sourceFile.getFileName(), ".map", "");
    String sourceName = filename + ".source";
    String source = sourceFile.getSource();

    return nashornCompiler.compile(filename, sourceName, source, options);
  }
}
