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
package net.codestory.http.templating;

import net.codestory.http.compilers.SourceFile;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlFrontMatterTest {
  private YamlFrontMatter parse(String path, String... lines) {
    return YamlFrontMatter.parse(new SourceFile(Paths.get(path), String.join("\n", lines)));
  }

  @Test
  public void should_read_empty_file() {
    YamlFrontMatter parsed = parse("empty", "");

    assertThat((Object) parsed.getPath()).isEqualTo(Paths.get("empty"));
    assertThat(parsed.getContent()).isEmpty();
    assertThat(parsed.getVariables())
      .hasSize(3)
      .containsEntry("content", "")
      .containsEntry("path", Paths.get("empty"))
      .containsEntry("name", "empty");
  }

  @Test
  public void should_read_file_without_headers() {
    YamlFrontMatter parsed = parse("folder/file.md",
      "CONTENT"
    );

    assertThat((Object) parsed.getPath()).isEqualTo(Paths.get("folder/file.md"));
    assertThat(parsed.getContent()).isEqualTo("CONTENT");
    assertThat(parsed.getVariables())
      .hasSize(3)
      .containsEntry("content", "CONTENT")
      .containsEntry("path", Paths.get("folder/file.md"))
      .containsEntry("name", "file");
  }

  @Test
  public void should_read_header_variables() {
    YamlFrontMatter parsed = parse("",
      "---",
      "layout: standard",
      "title: CodeStory - Devoxx Fight",
      "---",
      "BODY"
    );

    assertThat(parsed.getContent()).isEqualTo("BODY");
    assertThat(parsed.getVariables())
      .containsEntry("content", "BODY")
      .containsEntry("layout", "standard")
      .containsEntry("title", "CodeStory - Devoxx Fight");
  }

  @Test
  public void should_ignore_commented_variable() {
    YamlFrontMatter parsed = parse("",
      "---",
      "#layout: standard",
      "title: CodeStory - Devoxx Fight",
      "---",
      "CONTENT"
    );

    assertThat(parsed.getVariables())
      .doesNotContainEntry("layout", "standard")
      .doesNotContainEntry("#layout", "standard")
      .containsEntry("title", "CodeStory - Devoxx Fight");
  }

  @Test
  public void escape_strings_with_quotes() {
    YamlFrontMatter parsed = parse("",
      "---",
      "title: \'{{Code}} Fight by Code-Story\'",
      "---",
      "CONTENT"
    );

    assertThat(parsed.getVariables())
      .containsEntry("title", "{{Code}} Fight by Code-Story");
  }

  @Test
  public void complex_yaml() {
    YamlFrontMatter parsed = parse("",
      " ",
      "  ---",
      "products: ",
      " - name: PROD1",
      " - name: PROD2",
      "---  ",
      "CONTENT"
    );

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> products = (List<Map<String, Object>>) parsed.getVariables().get("products");
    assertThat(products).hasSize(2);
    assertThat(products.get(0)).containsEntry("name", "PROD1");
    assertThat(products.get(1)).containsEntry("name", "PROD2");
  }

  @Test
  public void ignore_dashes_in_content() {
    YamlFrontMatter parsed = parse("",
      "---",
      "title: TITLE",
      "---",
      "START",
      "---",
      "END"
    );

    assertThat(parsed.getContent()).isEqualTo("START\n---\nEND");
    assertThat(parsed.getVariables()).containsEntry("title", "TITLE");
  }
}
