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

import java.io.*;

import org.mozilla.javascript.*;

import com.google.common.io.*;

class CoffeeCompiler {
  public String compile(String source) throws IOException {
    Scriptable globalScope = initWithCoffeeScriptScript();

    Context context = Context.enter();
    try {
      Scriptable scope = context.newObject(globalScope);
      scope.setParentScope(globalScope);
      scope.put("coffeeScriptSource", scope, source);
      return (String) context.evaluateString(scope, String.format("CoffeeScript.compile(coffeeScriptSource, %s);", "{bare: true}"), "JCoffeeScriptCompiler", 0, null);
    } catch (JavaScriptException e) {
      throw new IOException("Unable to compile coffee", e);
    } finally {
      Context.exit();
    }
  }

  private Scriptable initWithCoffeeScriptScript() throws IOException {
    String coffeeScriptJs = CharStreams.toString(Resources.newReaderSupplier(Resources.getResource("coffee/coffee-script.js"), UTF_8));

    Context context = Context.enter();
    try {
      Scriptable globalScope = context.initStandardObjects();
      context.evaluateString(globalScope, coffeeScriptJs, "coffee-script.js", 0, null);
      return globalScope;
    } finally {
      Context.exit();
    }
  }
}