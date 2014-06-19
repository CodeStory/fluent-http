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

import java.nio.file.*;

import net.codestory.http.misc.*;

public class CoffeeCompiler implements Compiler {
  private final NashornCompiler nashornCompiler = new NashornCompiler(
    "META-INF/resources/webjars/coffee-script/1.7.1/coffee-script.min.js",
    "coffee-script/toJs.js");

  @Override
  public String compile(Path path, String source) {
    String javascript = nashornCompiler.compile(path, source);
    if (new Env().prodMode()) { // TODO: inject env instead.
      return javascript;
    }

    return addSourceMapping(javascript, path);
  }

  private static String addSourceMapping(String javascript, Path path) {
    return javascript + "\n//# sourceMappingURL=" + path.getFileName() + ".map";
  }
}