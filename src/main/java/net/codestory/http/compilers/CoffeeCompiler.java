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

import java.util.*;

import static java.util.Collections.singletonMap;

public class CoffeeCompiler implements Compiler {
  private final NashornCompiler nashornCompiler = NashornCompiler.get(
      "META-INF/resources/webjars/coffee-script/1.8.0/coffee-script.min.js",
      "coffee-script/toJs.js");

  private final boolean sourceMaps;

  public CoffeeCompiler(boolean prodMode) {
    this.sourceMaps = !prodMode;
  }

  @Override
  public String compile(SourceFile sourceFile) {
    Map<String, Object> options = singletonMap("__literate", sourceFile.hasExtension(".litcoffee"));

    String javascript = nashornCompiler.compile(sourceFile, options);

    if (sourceMaps) {
      return addSourceMapping(javascript, sourceFile);
    }

    return javascript;
  }

  private static String addSourceMapping(String javascript, SourceFile sourceFile) {
    return javascript + "\n//# sourceMappingURL=" + sourceFile.getFileName() + ".map";
  }
}
