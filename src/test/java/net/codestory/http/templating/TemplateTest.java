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

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import org.junit.*;

public class TemplateTest {
  @Test
  public void render() {
    assertThat(new Template("0variable.txt").render()).isEqualTo("0 variables");
    assertThat(new Template("1variable.txt").render("name", "Bob")).isEqualTo("Hello Bob");
    assertThat(new Template("2variables.txt").render("verb", "Hello", "name", "Bob")).isEqualTo("Hello Bob");
    assertThat(new Template("2variables.txt").render(new HashMap<String, Object>() {{
      put("verb", "Hello");
      put("name", 12);
    }})).isEqualTo("Hello 12");
  }

  @Test
  public void yaml_front_matter() {
    assertThat(new Template("indexYaml.html").render()).contains("Hello Yaml");
  }

  @Test
  public void layout_decorator() {
    assertThat(new Template("pageYaml.html").render()).contains("PREFIX_LAYOUT<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_LAYOUT");
    assertThat(new Template("pageYamlWithMarkdownLayout.html").render()).contains("TITLE: PREFIX_MD<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_MD");
  }

  @Test
  public void site_variables() {
    assertThat(new Template("useSiteVariables.html").render()).contains("Hello Bob");
  }
}
