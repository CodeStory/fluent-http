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
import static javax.script.ScriptContext.*;

import java.io.*;

import javax.script.*;

class AsciidocCompiler {
  private final CompiledScript coffeeToJs;
  private final Bindings bindings;

  public AsciidocCompiler() {
    ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

    try (Reader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("coffee/opal_asciidoctor.js"), UTF_8)) {
      coffeeToJs = ((Compilable) nashorn).compile(reader);
      bindings = nashorn.getBindings(ENGINE_SCOPE);
    } catch (IOException | ScriptException e) {
      throw new IllegalStateException(e);
    }
  }

  public synchronized String compile(String source) throws IOException {
    try {
      bindings.put("asciidocSource", source);
      return coffeeToJs.eval(bindings).toString();
    } catch (ScriptException e) {
      throw new IOException("Unable to compile coffee", e);
    }
  }
}