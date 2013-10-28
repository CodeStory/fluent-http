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

public class MarkdownCompilerTest {
  @Test
  public void empty() throws IOException {
    String html = Compiler.compile(Paths.get("empty.md"), "");

    assertThat(html).isEmpty();
  }

  @Test
  public void markdown_to_html() throws IOException {
    String html = Compiler.compile(Paths.get("file.md"), "This is **bold**");

    assertThat(html).isEqualTo("<p>This is <strong>bold</strong></p>\n");
  }

  @Test
  public void strikeout() throws IOException {
    String html = Compiler.compile(Paths.get("file.md"), "This is ~~deleted~~ text");

    assertThat(html).isEqualTo("<p>This is <s>deleted</s> text</p>\n");
  }

  @Test
  public void images() throws IOException {
    String html = Compiler.compile(Paths.get("file.md"), "![Alt text](/path/to/img.jpg)");

    assertThat(html).isEqualTo("<p><img src=\"/path/to/img.jpg\" alt=\"Alt text\" /></p>\n");
  }

  @Test
  public void extension() throws IOException {
    String html = Compiler.compile(Paths.get("file.markdown"), "## HEADER ## {#ID}");

    assertThat(html).isEqualTo("<h2 id=\"ID\">HEADER</h2>\n");
  }
}
