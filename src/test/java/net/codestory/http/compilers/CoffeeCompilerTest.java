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

import org.junit.*;
import org.junit.rules.*;

public class CoffeeCompilerTest {
  static CoffeeCompiler compiler = new CoffeeCompiler(false);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private String compile(String filename, String content) {
    return compiler.compile(Paths.get(filename), content);
  }

  @Test
  public void empty() {
    String js = compile("empty.coffee", "");

    assertThat(js).isEqualTo("\n\n//# sourceMappingURL=empty.coffee.map");
  }

  @Test
  public void to_javascript() {
    String js = compile("file.coffee", "life=42");

    assertThat(js).isEqualTo("var life;\n\nlife = 42;\n\n//# sourceMappingURL=file.coffee.map");
  }

  @Test
  public void for_performance_compile_coffee_to_js_only_once() {
    new CoffeeCompiler(false).compile(Paths.get("warmup.coffee"), "life=" + 0);

    long date1 = System.currentTimeMillis();
    for (int i = 1; i < 10; i++) {
      String js = new CoffeeCompiler(false).compile(Paths.get("file.coffee"), "life=" + i);
      assertThat(js).isNotEmpty();
    }
    long date2 = System.currentTimeMillis();
    assertThat(date2 - date1).isLessThan(5000);
  }

  @Test
  public void dont_set_sourcemap_in_prod_mode() {
    CoffeeCompiler compiler = new CoffeeCompiler(true);

    String js = compiler.compile(Paths.get("file.coffee"), "life=42");

    assertThat(js).isEqualTo("var life;\n\nlife = 42;\n");
  }

  @Test
  public void invalid_script() {
    thrown.expect(CompilerException.class);
    thrown.expectMessage("Unable to compile invalid.coffee:1:1: error: unexpected ==");

    compile("invalid.coffee", "===");
  }

  @Test
  public void report_line_number() {
    thrown.expectMessage("Unable to compile invalid.coffee:3:1: error: unexpected ==");

    compile("invalid.coffee", "\n\n===");
  }

  @Test
  public void literate_coffee() {
    String js = compile("file.litcoffee", "Comment text\n" +
      "\n" +
      "    life=42\n" +
      "\n");

    assertThat(js).isEqualTo("var life;\n\nlife = 42;\n\n//# sourceMappingURL=file.litcoffee.map");
  }
}
