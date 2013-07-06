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

import static java.util.Arrays.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import org.jcoffeescript.*;

public class CoffeeScriptCompiler {
  public String compile(Path path) throws IOException {
    try {
      String coffee = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

      return new JCoffeeScriptCompiler(asList(Option.BARE)).compile(coffee);
    } catch (JCoffeeScriptCompileException e) {
      throw new IOException("Unable to compile less file", e);
    }
  }
}
