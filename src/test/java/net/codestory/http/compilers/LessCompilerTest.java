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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;

import org.junit.*;

public class LessCompilerTest {
  Compiler lessCompiler = Compiler.LESS;

  @Test
  public void empty_less() throws IOException {
    String css = lessCompiler.compile("");

    assertThat(css).isEmpty();
  }

  @Test
  public void less_to_css() throws IOException {
    String css = lessCompiler.compile("body { h1 { color: red; } }");

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n");
  }
}
