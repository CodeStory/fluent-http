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

import java.nio.file.*;
import java.util.*;

import static java.util.Collections.singletonMap;

public class CoffeeSourceMapCompiler implements Compiler {
  private final NashornCompiler nashornCompiler = new NashornCompiler(
    "META-INF/resources/webjars/coffee-script/1.8.0/coffee-script.min.js",
    "coffee-script/toSourceMap.js");

  @Override
  public String compile(Path path, String source) {
    Map<String, Object> options = singletonMap("__literate", path.toString().endsWith(".litcoffee"));

    return nashornCompiler.compile(path, source, options);
  }
}
