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

import org.junit.*;

public class TemplateTest {
  @Test
  public void render() {
    assertThat(new Template("0variable.txt").render(Model.of())).isEqualTo("0 variables");
    assertThat(new Template("1variable.txt").render(Model.of("name", "Bob"))).isEqualTo("Hello Bob");
    assertThat(new Template("2variables.txt").render(Model.of("verb", "Hello", "name", "Bob"))).isEqualTo("Hello Bob");
  }

  @Test
  public void yaml_front_matter() {
    assertThat(new Template("indexYaml.html").render(Model.of())).contains("Hello Yaml");
  }

  @Test
  public void layout_decorator() {
    assertThat(new Template("pageYaml.html").render(Model.of())).contains("PREFIX_LAYOUT<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_LAYOUT");
    assertThat(new Template("pageYamlWithMarkdownLayout.html").render(Model.of())).contains("<em>TITLE</em>: PREFIX_MD<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_MD");
  }

  @Test
  public void site_variables() {
    assertThat(new Template("useSiteVariables.html").render(Model.of())).contains("Hello, customer Bob wants to buy p1 for parkr");
  }

  @Test
  public void markdown_list() {
    assertThat(new Template("list.md").render(Model.of())).contains("<ul>\n<li><p>Doc</p>\n</li>\n<li><p>Grumpy</p>\n</li>\n<li><p>Happy</p>\n</li>\n</ul>");
  }

  @Test
  public void default_layout() {
    assertThat(new Template("minimal.html").render(Model.of())).isEqualTo("" +
        "<!DOCTYPE html>\n" +
        "<html lang=\"en\">\n" +
        "<head>\n" +
        "  <meta charset=\"UTF-8\">\n" +
        "  <title></title>\n" +
        "  \n" +
        "  \n" +
        "  \n" +
        "  \n" +
        "  \n" +
        "  \n" +
        "</head>\n" +
        "<body>\n" +
        "Hello World\n" +
        "</body>\n" +
        "</html>\n");
  }

  @Test
  public void standard_head_fields() {
    assertThat(new Template("full_header").render(Model.of())).isEqualTo("<!DOCTYPE html>\n" +
        "<html lang=\"FR\" ng-app=\"app\">\n" +
        "<head>\n" +
        "  <meta charset=\"UTF-8\">\n" +
        "  <title>TITLE</title>\n" +
        "  <meta name=\"viewport\" content=\"viewport\">\n" +
        "  <meta name=\"keywords\" content=\"keyword1, keyword2\">\n" +
        "  <meta name=\"description\" content=\"description\">\n" +
        "  <meta name=\"author\" content=\"author\">\n" +
        "  <link rel=\"stylesheet\" href=\"style.less\">\n" +
        "  \n" +
        "  <link rel=\"stylesheet\" href=\"style1.css\">\n" +
        "  <link rel=\"stylesheet\" href=\"style2.css\">\n" +
        "</head>\n" +
        "<body>\n" +
        "Hello World\n" +
        "</body>\n" +
        "</html>\n");
  }
}
