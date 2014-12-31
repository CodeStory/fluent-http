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

import static java.util.Collections.*;
import static net.codestory.http.misc.Env.DEFAULT_APP_FOLDER;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.*;

import org.junit.*;

public class TemplateTest {
  static Env env = prodMode();
  static Resources resources = new Resources(env);
  static CompilerFacade compilerFacade = new CompilerFacade(env, resources);

  String render(String name, Map<String, Object> model) {
    return new Template(resources, name).render(model, compilerFacade);
  }

  String render(String name) {
    return render(name, emptyMap());
  }

  @Test
  public void empty_model() {
    String content = render("0variable.txt");

    assertThat(content).isEqualTo("0 variables");
  }

  @Test
  public void one_variable() {
    String content = render("1variable.txt", singletonMap("name", "Bob"));

    assertThat(content).isEqualTo("Hello Bob");
  }

  @Test
  public void two_variables() {
    Map<String, Object> model = new HashMap<>();
    model.put("verb", "Hello");
    model.put("name", "Bob");

    String content = render("2variables.txt", model);

    assertThat(content).isEqualTo("Hello Bob");
  }

  @Test
  public void yaml_front_matter() {
    assertThat(render("indexYaml.html")).contains("Hello Yaml");
  }

  @Test
  public void layout_decorator() {
    assertThat(render("pageYaml.html")).contains("PREFIX_LAYOUT<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_LAYOUT");
    assertThat(render("pageYamlWithMarkdownLayout.html")).contains("<em>TITLE</em>: PREFIX_MD<div>_PREFIX_TEXT_SUFFIX_</div>SUFFIX_MD");
    assertThat(render("markdownWithLayout.md")).startsWith("<!DOCTYPE html>").contains("<p>Hello World</p>").endsWith("</html>\n");
  }

  @Test
  public void markdown_list() {
    String html = render("list.md");

    assertThat(ignoreLineEndings(html)).contains("<ul><li><p>Doc</p></li><li><p>Grumpy</p></li><li><p>Happy</p></li></ul>");
  }

  @Test
  public void default_layout() {
    String html = render("minimal.html");

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
    String html = render("full_header");

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
        "<script src=\"angular.js\"></script>" +
        "<script src=\"app.js\"></script>" +
        "</html>");
  }

  private static String ignoreLineEndings(String text) {
    return text.replaceAll("[\\n\\r]", "");
  }

  private static Env prodMode() {
    return new Env(DEFAULT_APP_FOLDER, true, false, false, false);
  }
}
