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

import static java.nio.charset.StandardCharsets.*;
import static javax.script.ScriptContext.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import net.codestory.http.io.*;

import javax.script.*;
import jdk.nashorn.api.scripting.*;
import jdk.nashorn.internal.runtime.options.*;

public final class NashornCompiler {
  private static final ConcurrentMap<String, NashornCompiler> CACHE_BY_SCRIPT = new ConcurrentHashMap<>();

  private final CompiledScript compiledScript;
  private final Bindings bindings;

  private NashornCompiler(String script) {
    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    String engineVersion = factory.getEngineVersion();
    String cacheLocation = Paths.get(System.getProperty("user.home"), ".code-story", "nashorn_code_cache_" + engineVersion).toFile().getAbsolutePath();
    System.setProperty("nashorn.persistent.code.cache", cacheLocation);

    ScriptEngine nashorn = factory.getScriptEngine(nashornOptions());

    try {
      compiledScript = ((Compilable) nashorn).compile(script);
      bindings = nashorn.getBindings(ENGINE_SCOPE);
    } catch (ScriptException e) {
      throw new IllegalStateException("Unable to compile javascript", e);
    }
  }

  private static String[] nashornOptions() {
    List<String> options = new ArrayList<>();

    // --optimistic-types=true in JDK 8u40 slows down everything
    if (canUseOption("--persistent-code-cache")) {
      options.add("--persistent-code-cache=true");
    }
    if (canUseOption("--lazy-compilation")) {
      options.add("--lazy-compilation=false");
    }

    return options.stream().toArray(String[]::new);
  }

  private static boolean canUseOption(String name) {
    return Options.getValidOptions().stream().anyMatch(validOption -> validOption.getName().equals(name));
  }

  public static NashornCompiler get(String... scriptPaths) {
    String script = readScripts(scriptPaths);
    return CACHE_BY_SCRIPT.computeIfAbsent(script, NashornCompiler::new);
  }

  private static String readScripts(String... scriptPaths) {
    StringBuilder concatenatedScript = new StringBuilder();

    for (String scriptPath : scriptPaths) {
      try (InputStream input = InputStreams.getResourceAsStream(scriptPath)) {
        String content = InputStreams.readString(input, UTF_8);

        concatenatedScript.append(content).append("\n");
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read script " + scriptPath, e);
      }
    }

    return concatenatedScript.toString();
  }

  public synchronized String compile(String filename, String sourceName, String source, Map<String, Object> options) {
    bindings.put("__filename", filename);
    bindings.put("__sourcename", sourceName);
    bindings.put("__source", source);
    options.forEach((name, value) -> bindings.put(name, value));

    try {
      return compiledScript.eval(bindings).toString();
    } catch (ScriptException e) {
      String message = cleanMessage(filename, e.getCause().getMessage());
      throw new CompilerException(message);
    }
  }

  private static String cleanMessage(String filename, String message) {
    return message.replace(
      "Unable to compile CoffeeScript [stdin]:",
      "Unable to compile " + filename + ":"
    );
  }
}
