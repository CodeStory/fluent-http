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
package net.codestory.http.markdown;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownCompilerTest {
  private String compile(String content) {
    return MarkdownCompiler.INSTANCE.compile(content);
  }

  @Test
  public void empty() {
    String html = compile("");

    assertThat(html).isEmpty();
  }

  @Test
  public void markdown_to_html() {
    String html = compile("This is **bold**");

    assertThat(html).isEqualTo("<p>This is <strong>bold</strong></p>\n");
  }

  @Test
  public void strikeout() {
    String html = compile("This is ~~deleted~~ text");

    assertThat(html).isEqualTo("<p>This is <s>deleted</s> text</p>\n");
  }

  @Test
  public void images() {
    String html = compile("![Alt text](/path/to/img.jpg)");

    assertThat(html).isEqualTo("<p><img src=\"/path/to/img.jpg\" alt=\"Alt text\" /></p>\n");
  }

  @Test
  public void extension() {
    String html = compile("## HEADER ## {#ID}");

    assertThat(html).isEqualTo("<h2 id=\"ID\">HEADER</h2>\n");
  }

  @Test
  public void code_block() {
    String html = compile("``` java\nnop\n```\n");

    assertThat(html).isEqualTo("<pre><code class=\"java\">nop\n</code></pre>\n");
  }

  @Test
  public void google_maps() {
    String html = compile("<@15 rue de la paix Paris>");

    assertThat(html).isEqualTo("<p><a href=\"https://maps.google.com/maps?q=15+rue+de+la+paix+Paris\">15 rue de la paix Paris</a></p>\n");
  }

  @Test
  public void formula_as_png() {
    String html = compile("%%% formula\n(1+2)\n%%%\n");

    assertThat(html).isEqualTo("<img src=\"http://latex.codecogs.com/png.download?%281%2B2%29\" />");
  }

  @Test
  public void table() {
    String html = compile("%%% table\nH1|H2|H3\n%%%\n");

    assertThat(html).isEqualTo("<table>\n<tr><th>H1</th><th>H2</th><th>H3</th></tr>\n</table>\n");
  }
}
