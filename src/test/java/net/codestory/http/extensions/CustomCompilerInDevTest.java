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
package net.codestory.http.extensions;

import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.misc.Env;
import net.codestory.http.testhelpers.AbstractDevWebServerTest;
import org.junit.Test;

import java.nio.file.Path;

import static java.util.stream.Collectors.joining;
import static net.codestory.http.misc.Fluent.ofChars;

public class CustomCompilerInDevTest extends AbstractDevWebServerTest {
  @Test
  public void custom_compiler() {
    server.configure(routes -> routes
      .setExtensions(new Extensions() {
        @Override
        public void configureCompilers(CompilerFacade compilers, Env env) {
          compilers.registerCompiler(SortContentCompiler::new, ".html", ".script");
        }
      }));

    get("/extensions/custom_compiler.html").produces("HWdellloor");
    get("/extensions/custom_compiler.script").produces("HWdellloor");
    get("/extensions/custom_compiler.html.source").produces("HelloWorld");
    get("/extensions/custom_compiler.script.source").produces("HelloWorld");
  }

  static class SortContentCompiler implements net.codestory.http.compilers.Compiler {
    @Override
    public String compile(Path path, String source) {
      return ofChars(source).sorted().collect(joining());
    }
  }
}
