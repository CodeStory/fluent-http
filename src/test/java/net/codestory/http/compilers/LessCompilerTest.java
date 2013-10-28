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

import java.io.*;
import java.nio.file.*;

import org.junit.*;

public class LessCompilerTest {
  @Test
  public void empty_less() throws IOException {
    String css = Compiler.compile(Paths.get("empty.less"), "");

    assertThat(css).isEqualTo("/*# sourceMappingURL=empty.css.map */\n");
  }

  @Test
  public void less_to_css() throws IOException {
    String css = Compiler.compile(Paths.get("file.less"), "body { h1 { color: red; } }");

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=file.css.map */\n");
  }

  @Test
  public void source_map() throws IOException {
    String css = Compiler.compile(Paths.get("/path/file.css.map"), "body { h1 { color: red; } }");

    assertThat(css).isEqualTo("{\n" +
        "\"version\":3,\n" +
        "\"file\":\"/path/file.css.css\",\n" +
        "\"lineCount\":1,\n" +
        "\"mappings\":\"AAAAA,I,CAAOC;\",\n" +
        "\"sources\":[\"/path/file.css.map\"],\n" +
        "\"names\":[\"body\",\"h1\"]\n" +
        "}\n");
  }
}
