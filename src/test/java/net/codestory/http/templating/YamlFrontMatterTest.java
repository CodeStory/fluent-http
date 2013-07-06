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

import org.junit.*;

import com.google.common.base.*;

public class YamlFrontMatterTest {
  YamlFrontMatter yamlFrontMatter = new YamlFrontMatter();

  @Test
  public void should_read_empty_file() {
    String content = fileContent("");

    ContentWithVariables parsed = yamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).isEmpty();
    assertThat(parsed.getContent()).isEmpty();
  }

  @Test
  public void should_read_file_without_headers() {
    String content = fileContent("CONTENT");

    ContentWithVariables parsed = yamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).isEmpty();
    assertThat(parsed.getContent()).isEqualTo("CONTENT");
  }

  @Test
  public void should_read_header_variables() {
    String content = fileContent(
        "---",
        "layout: standard",
        "title: CodeStory - Devoxx Fight",
        "---",
        "CONTENT");

    ContentWithVariables parsed = yamlFrontMatter.parse(content);

    assertThat(parsed.getVariables()).includes(
        entry("layout", "standard"),
        entry("title", "CodeStory - Devoxx Fight"));
    assertThat(parsed.getContent()).isEqualTo("CONTENT");
  }

  @Test
  public void should_ignore_commented_variable() {
    String content = fileContent(
        "---",
        "#layout: standard",
        "title: CodeStory - Devoxx Fight",
        "---",
        "CONTENT");

    ContentWithVariables parsed = yamlFrontMatter.parse(content);

    assertThat(parsed.getVariables())
        .excludes(entry("layout", "standard"))
        .excludes(entry("#layout", "standard"))
        .includes(entry("title", "CodeStory - Devoxx Fight"));
  }

  static String fileContent(String... lines) {
    return Joiner.on("\n").join(lines);
  }
}
