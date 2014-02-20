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

import javax.script.*;

class CoffeeCompiler extends AbstractNashornCompiler implements Compiler {
  public CoffeeCompiler() {
    super("META-INF/resources/webjars/coffee-script/1.7.0/coffee-script.min.js", "coffee-script/compile.js");
  }

  @Override
  protected void setBindings(Bindings bindings, String source) {
    bindings.put("coffeeScriptSource", source);
  }

  @Override
  protected String decorateScript(String source) {
    return source;
  }

  @Override
  public String compile(Path path, String source) throws IOException {
    return compile(source);
  }
}