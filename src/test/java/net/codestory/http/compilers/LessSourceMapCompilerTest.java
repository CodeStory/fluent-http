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

public class LessSourceMapCompilerTest {
  LessSourceMapCompiler compiler = new LessSourceMapCompiler();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void source_map() {
    String css = compiler.compile(Paths.get("/path/file.less.map"), "body { h1 { color: red; } }");

    assertThat(css).isEqualTo("{\n" +
      "\"version\":3,\n" +
      "\"file\":\"/path/file.less.source\",\n" +
      "\"lineCount\":1,\n" +
      "\"mappings\":\"AAAAA,I,CAAOC;\",\n" +
      "\"sources\":[\"/path/file.less.source\"],\n" +
      "\"names\":[\"body\",\"h1\"]\n" +
      "}\n");
  }

  @Test
  public void invalid_file() {
    thrown.expect(CompilerException.class);
    thrown.expectMessage(
      "Unable to compile less invalid.css.map: 3 error(s) occurred:\n" +
        "ERROR 1:6 no viable alternative at input 'body' in ruleset (which started at 1:1)\n" +
        "ERROR 1:6 required (...)+ loop did not match anything at input 'body' in selectors (which started at 1:6)\n" +
        "...");

    compiler.compile(Paths.get("invalid.css.map"), "body body");
  }
}
