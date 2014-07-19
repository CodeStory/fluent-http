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

import static org.assertj.core.api.Assertions.*;

import java.nio.file.*;

import org.junit.*;
import org.junit.rules.*;

public class LessCompilerTest {
  private static LessCompiler compiler = new LessCompiler();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void empty() {
    String css = compiler.compile(Paths.get("empty.less"), "");

    assertThat(css).isEqualTo("/*# sourceMappingURL=empty.less.map */");
  }

  @Test
  public void to_css() {
    String css = compiler.compile(Paths.get("file.less"), "body { h1 { color: red; } }");

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=file.less.map */");
  }

  @Test
  public void import_less() {
    String css = compiler.compile(Paths.get("style.less"), "@import 'assets/style.less';");

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=style.less.map */");
  }

  @Test
  public void import_less_from_webjar() {
    String css = compiler.compile(Paths.get("style.less"), "@import '/webjars/bootstrap/3.2.0/less/bootstrap.less';");

    assertThat(css).isNotEmpty().doesNotContain("@import");
  }

  @Test
  public void invalid_file() {
    thrown.expect(CompilerException.class);
    thrown.expectMessage(
      "Unable to compile less invalid.less: 3 error(s) occurred:\n" +
        "ERROR 1:6 no viable alternative at input 'body' in ruleset (which started at 1:1)\n" +
        "ERROR 1:6 required (...)+ loop did not match anything at input 'body' in selectors (which started at 1:6)\n" +
        "...");

    compiler.compile(Paths.get("invalid.less"), "body body");
  }
}
