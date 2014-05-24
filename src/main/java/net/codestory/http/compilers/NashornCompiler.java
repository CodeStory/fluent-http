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

import javax.script.*;
import java.io.*;
import java.nio.file.*;

import net.codestory.http.io.*;

public final class NashornCompiler {
  private final CompiledScript compiledScript;
  private final Bindings bindings;

  NashornCompiler(String... scriptPaths) {
    String script = readScripts(scriptPaths);

    ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      compiledScript = ((Compilable) nashorn).compile(script);
      bindings = nashorn.getBindings(ENGINE_SCOPE);
    } catch (ScriptException e) {
      throw new IllegalStateException("Unable to compile javascript", e);
    }
  }

  private String readScripts(String... scriptPaths) {
    StringBuilder concatenatedScript = new StringBuilder();

    for (String scriptPath : scriptPaths) {
      try (InputStream input = Resources.getResourceAsStream(scriptPath)) {
        String content = InputStreams.readString(input, UTF_8);

        concatenatedScript.append(content).append("\n");
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read script " + scriptPath, e);
      }
    }

    return concatenatedScript.toString();
  }

  public synchronized String compile(Path path, String source) {
    bindings.put("__filename", path.getFileName());
    bindings.put("__source", source);

    try {
      return compiledScript.eval(bindings).toString();
    } catch (ScriptException e) {
      String message = cleanMessage(path, e.getCause().getMessage());
      throw new CompilerException(message);
    }
  }

  private static String cleanMessage(Path path, String message) {
    return message.replace(
      "Unable to compile CoffeeScript [stdin]:",
      "Unable to compile " + path + ":"
    );
  }
}