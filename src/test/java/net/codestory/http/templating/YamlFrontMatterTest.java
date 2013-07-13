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
package net.codestory.http.templating;

import static org.fest.assertions.Assertions.*;
import static org.fest.assertions.MapAssert.*;

import java.util.*;

import org.junit.*;

public class YamlFrontMatterTest {
  @Test
  public void should_read_empty_file() {
    String content = content("");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).isEmpty();
    assertThat(parsed.getContent()).isEmpty();
  }

  @Test
  public void should_read_file_without_headers() {
    String content = content("CONTENT");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).isEmpty();
    assertThat(parsed.getContent()).isEqualTo("CONTENT");
  }

  @Test
  public void should_read_header_variables() {
    String content = content(
        "---",
        "layout: standard",
        "title: CodeStory - Devoxx Fight",
        "---",
        "CONTENT");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).includes(
        entry("layout", "standard"),
        entry("title", "CodeStory - Devoxx Fight"));
    assertThat(parsed.getContent()).isEqualTo("CONTENT");
  }

  @Test
  public void should_ignore_commented_variable() {
    String content = content(
        "---",
        "#layout: standard",
        "title: CodeStory - Devoxx Fight",
        "---",
        "CONTENT");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    assertThat(parsed.getVariables())
        .excludes(entry("layout", "standard"))
        .excludes(entry("#layout", "standard"))
        .includes(entry("title", "CodeStory - Devoxx Fight"));
  }

  @Test
  public void escape_strings_with_quotes() {
    String content = content(
        "---",
        "title: \'{{Code}} Fight by Code-Story\'",
        "---",
        "CONTENT");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    assertThat(parsed.getVariables().get("title")).isEqualTo("{{Code}} Fight by Code-Story");
  }

  @Test
  public void complex_yaml() {
    String content = content(
        "---",
        "products: ",
        " - name: PROD1",
        " - name: PROD2",
        "---",
        "CONTENT");

    YamlFrontMatter parsed = YamlFrontMatter.parse(content);

    List<Map<String, Object>> products = (List<Map<String, Object>>) parsed.getVariables().get("products");
    assertThat(products).hasSize(2);
    assertThat(products.get(0)).includes(entry("name", "PROD1"));
    assertThat(products.get(1)).includes(entry("name", "PROD2"));
  }

  static String content(String... lines) {
    return String.join("\n", lines);
  }
}
