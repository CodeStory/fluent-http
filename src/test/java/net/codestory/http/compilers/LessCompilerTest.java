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

import static org.assertj.core.api.Assertions.*;

import java.nio.file.*;

import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import org.junit.*;
import org.junit.rules.*;

public class LessCompilerTest {
  static Resources resources = new Resources(Env.prod());
  static LessCompiler compiler = new LessCompiler(resources, false);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private String compile(String filename, String content) {
    return compiler.compile(new SourceFile(Paths.get(filename), content));
  }

  @Test
  public void empty() {
    String css = compile("empty.less", "");

    assertThat(css).isEqualTo("\n/*# sourceMappingURL=data:application/json;base64,ewoidmVyc2lvbiI6MywKImZpbGUiOiJlbXB0eS5jc3MiLAoibGluZUNvdW50IjoxLAoibWFwcGluZ3MiOiI7IiwKInNvdXJjZXMiOltdLAoic291cmNlc0NvbnRlbnQiOltdLAoibmFtZXMiOltdCn0K */\n");
  }

  @Test
  public void to_css() {
    String css = compile("file.less", "body { h1 { color: red; } }");

    assertThat(css).isEqualTo(
      "body h1 {\n  color: red;\n}\n" +
        "/*# sourceMappingURL=data:application/json;base64,ewoidmVyc2lvbiI6MywKImZpbGUiOiJmaWxlLmNzcyIsCiJsaW5lQ291bnQiOjEsCiJtYXBwaW5ncyI6IkFBQUFBLEksQ0FBT0M7IiwKInNvdXJjZXMiOlsiZmlsZS5sZXNzIl0sCiJzb3VyY2VzQ29udGVudCI6W251bGxdLAoibmFtZXMiOlsiYm9keSIsImgxIl0KfQo= */\n"
    );
  }

  @Test
  public void no_sourcemap_in_prod_mode() {
    LessCompiler compiler = new LessCompiler(resources, true);

    String css = compiler.compile(new SourceFile(Paths.get("file.less"), "body { h1 { color: red; } }"));

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n");
  }

  @Test
  public void import_less() {
    String css = compile("style.less", "@import 'assets/style.less';");

    assertThat(css).isEqualTo(
      "body h1 {\n  color: red;\n}\n" +
        "/*# sourceMappingURL=data:application/json;base64,ewoidmVyc2lvbiI6MywKImZpbGUiOiJzdHlsZS5jc3MiLAoibGluZUNvdW50IjoxLAoibWFwcGluZ3MiOiJBQUFBQSxJLENBQ0VDOyIsCiJzb3VyY2VzIjpbImFzc2V0cy9zdHlsZS5sZXNzIl0sCiJzb3VyY2VzQ29udGVudCI6W251bGxdLAoibmFtZXMiOlsiYm9keSIsImgxIl0KfQo= */\n"
    );
  }

  @Test
  public void import_less_from_webjar() {
    String css = compile("style.less", "@import '/webjars/bootstrap/3.3.2-2/less/bootstrap.less';");

    assertThat(css).isNotEmpty().doesNotContain("@import");
  }

  @Test
  public void invalid_file() {
    thrown.expect(CompilerException.class);
    thrown.expectMessage(
      "Unable to compile less invalid.less: 3 error(s) occurred:\n" +
        "ERROR 1:6 no viable alternative at input 'body' in ruleset (which started at 1:1)\n" +
        " 1: body body\n\n" +
        "ERROR 1:6 required (...)+ loop did not match anything at input 'body' in selectors (which started at 1:6)\n" +
        " 1: body body\n\n" +
        "...");

    compile("invalid.less", "body body");
  }
}
