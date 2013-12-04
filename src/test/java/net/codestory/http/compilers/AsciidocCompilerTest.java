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

public class AsciidocCompilerTest {
  @Test
  public void empty() {
    String html = Compiler.compile(Paths.get("empty.asciidoc"), "");

    assertThat(html).isEqualTo("\n");
  }

  @Test
  public void to_html() {
    String html = Compiler.compile(Paths.get("doc.asciidoc"), "== Title\ntext http://asciidoc.org[AsciiDoc]");

    assertThat(html)
        .contains("<h2 id=\"_title\">Title</h2>")
        .contains("<p>text <a href=\"http://asciidoc.org\">AsciiDoc</a></p>");
  }
}
