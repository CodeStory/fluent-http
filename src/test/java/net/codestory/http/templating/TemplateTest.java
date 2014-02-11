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
    assertThat(new Template("0variable.txt").render().content()).isEqualTo("0 variables");
    assertThat(new Template("1variable.txt").render(Model.of("name", "Bob")).content()).isEqualTo("Hello Bob");
    assertThat(new Template("2variables.txt").render(Model.of("verb", "Hello", "name", "Bob")).content()).isEqualTo("Hello Bob");
  }

  @Test
  public void yaml_front_matter() {
    assertThat(new Template("indexYaml.html").render().content()).contains("Hello Yaml");
  }

  @Test
  public void layout_decorator() {
    assertThat(new Template("pageYaml.html").render().content()).contains("PREFIX_LAYOUT<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_LAYOUT");
    assertThat(new Template("pageYamlWithMarkdownLayout.html").render().content()).contains("<em>TITLE</em>: PREFIX_MD<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_MD");
  }

  @Test
  public void site_variables() {
    assertThat(new Template("useSiteVariables.html").render().content()).contains("Hello, customer Bob wants to buy p1 for parkr");
  }

  @Test
  public void markdown_list() {
    String html = new Template("list.md").render().content();

    assertThat(ignoreLineEndings(html)).contains("<ul><li><p>Doc</p></li><li><p>Grumpy</p></li><li><p>Happy</p></li></ul>");
  }

  @Test
  public void default_layout() {
    String html = new Template("minimal.html").render().content();

    assertThat(ignoreLineEndings(html)).isEqualTo("" +
        "<!DOCTYPE html>" +
        "<html lang=\"en\">" +
        "<head>" +
        "  <meta charset=\"UTF-8\">" +
        "  <title></title>" +
        "  " +
        "  " +
        "  " +
        "  " +
        "  " +
        "  " +
        "</head>" +
        "<body>" +
        "Hello World" +
        "</body>" +
        "</html>");
  }

  @Test
  public void standard_head_fields() {
    String html = new Template("full_header").render().content();

    assertThat(ignoreLineEndings(html)).isEqualTo("<!DOCTYPE html>" +
        "<html lang=\"FR\" ng-app=\"app\">" +
        "<head>" +
        "  <meta charset=\"UTF-8\">" +
        "  <title>TITLE</title>" +
        "  <meta name=\"viewport\" content=\"viewport\">" +
        "  <meta name=\"keywords\" content=\"keyword1, keyword2\">" +
        "  <meta name=\"description\" content=\"description\">" +
        "  <meta name=\"author\" content=\"author\">" +
        "  <link rel=\"stylesheet\" href=\"style.less\">" +
        "  " +
        "  <link rel=\"stylesheet\" href=\"style1.css\">" +
        "  <link rel=\"stylesheet\" href=\"style2.css\">" +
        "</head>" +
        "<body>" +
        "Hello World" +
        "</body>" +
        "</html>");
  }

  private static String ignoreLineEndings(String text) {
    return text.replaceAll("[\\n\\r]", "");
  }
}
