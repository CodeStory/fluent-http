/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.Resources;
import net.codestory.http.markdown.*;
import net.codestory.http.misc.*;
import net.codestory.http.templating.helpers.*;

import org.junit.*;

import com.github.jknack.handlebars.*;

public class HandlebarsCompilerTest {
  static Env env = Env.prod();
  static Resources resources = new Resources(env);
  static MarkdownCompiler markdownCompiler = new MarkdownCompiler();
  static HandlebarsCompiler compiler = new HandlebarsCompiler(env, resources, new CompilerFacade(env, resources), markdownCompiler);

  private String compile(String content, Map<String, Object> variables) throws IOException {
    return compiler.compile(content, variables);
  }

  @Test
  public void compile() throws IOException {
    String result = compile("-[[greeting]]-", map("greeting", "Hello"));

    assertThat(result).isEqualTo("-Hello-");
  }

  @Test
  public void partials() throws IOException {
    String result = compile("-[[>partial.txt]] [[>partial.txt]]-", map("name", "Bob"));

    assertThat(result).isEqualTo("-Hello Bob Hello Bob-");
  }

  @Test
  public void partial_with_context() throws IOException {
    String result = compile("[[>partialWithContext ctx]]", map("ctx", map("firstName", "Bob", "age", "42")));

    assertThat(result).isEqualTo("Hello Bob/42");
  }

  @Test
  public void partial_with_loop() throws IOException {
    String result = compile("[[>partialWithLoop ctx]]", map("ctx", map("terminal", false, "name", map("name", "bob", "terminal", true))));

    String content = String.format("Hello%1$s%1$s  Hello%1$s%1$s  bob%1$s%1$s%1$s%1$s", System.lineSeparator());
    assertThat(result).isEqualTo(content);
  }

  @Test
  public void find_partial() throws IOException {
    String result = compile("[[>partial]]", map("name", "Bob"));

    assertThat(result).isEqualTo("Hello Bob");
  }

  @Test
  public void markdown_partial() throws IOException {
    String result = compile("[[>map city]]", map("city", "Paris"));

    assertThat(result).isEqualTo("<p><a href=\"https://maps.google.com/maps?q=+Paris\"> Paris</a></p>\n");
  }

  @Test(expected = HandlebarsException.class)
  public void unknown_partial() throws IOException {
    compile("[[>unknown]]", map("", ""));
  }

  @Test
  public void string_helpers() throws IOException {
    String result = compile("Hello [[capitalizeFirst name]]", map("name", "joe"));

    assertThat(result).isEqualTo("Hello Joe");
  }

  @Test
  public void java_getters_and_fields() throws IOException {
    String result = compile("[[bean.name]] is [[bean.age]]", map("bean", new JavaBean("Bob", 12)));

    assertThat(result).isEqualTo("Bob is 12");
  }

  @Test
  public void java_getter_method() throws IOException {
    String result = compile("[[bean.fullDescription]]", map("bean", new JavaBean("Bob", 12)));

    assertThat(result).isEqualTo("Bob-12");
  }

  @Test
  public void java_plain_method() throws IOException {
    String result = compile("[[bean.description]]", map("bean", new JavaBean("Bob", 12)));

    assertThat(result).isEqualTo("Bob");
  }

  @Test
  public void each() throws IOException {
    String result = compile("[[#each list]][[.]][[/each]]", map("list", asList("A", "B")));

    assertThat(result).isEqualTo("AB");
  }

  @Test
  public void each_reverse() throws IOException {
    String result = compile("[[#each_reverse list]][[.]][[/each_reverse]]", map("list", asList("A", "B")));

    assertThat(result).isEqualTo("BA");
  }

  @Test
  public void values_by_key() throws IOException {
    Map<String, Object> variables = new TreeMap<>();
    variables.put("letters", asList("A", "B"));
    variables.put("descriptions", new TreeMap<String, Object>() {{
      put("A", "Letter A");
      put("B", "Letter B");
      put("C", "Letter C");
    }});

    String result = compile("[[#each_value descriptions letters]][[@key]]=[[.]][[/each_value]]", variables);

    assertThat(result).isEqualTo("A=Letter AB=Letter B");
  }

  @Test
  public void values_by_hash_key() throws IOException {
    Map<String, Object> variables = new TreeMap<>();
    variables.put("letters", new TreeMap<String, Object>() {{
      put("A", map("id", "idA"));
      put("B", map("id", "idB"));
    }});
    variables.put("descriptions", new TreeMap<String, Object>() {{
      put("A", "Description A");
      put("B", "Description B");
      put("C", "Description C");
    }});

    String result = compile("[[#each_value descriptions letters]][[@value.id]]=[[.]][[/each_value]]", variables);

    assertThat(result).isEqualTo("idA=Description AidB=Description B");
  }

  @Test
  public void unescaped_content() throws IOException {
    String result = compile("[[&html]]", map("html", "<div>Hello</div>"));

    assertThat(result).isEqualTo("<div>Hello</div>");
  }

  @Test
  public void custom_resolver() throws IOException {
    compiler.addResolver(new BasicResolver() {
      @Override
      public String tag() {
        return "additional";
      }

      @Override
      public Object resolve(Object context) {
        return "SUCCESS";
      }
    });

    String result = compile("[[additional]]", new TreeMap<>());

    assertThat(result).isEqualTo("SUCCESS");
  }

  @Test
  public void google_analytics_with_fixed_id() throws IOException {
    compiler.configure(hb -> hb.registerHelpers(new GoogleAnalyticsHelper("ID")));

    String result = compile("[[google_analytics]]", new TreeMap<>());

    assertThat(result).startsWith("<script>").contains("ID").endsWith("</script>");
  }

  @Test
  public void google_analytics_with_dynamic_id() throws IOException {
    compiler.configure(hb -> hb.registerHelpers(new GoogleAnalyticsHelper()));

    String result = compile("[[google_analytics UA]]", map("UA", "12345"));

    assertThat(result).startsWith("<script>").contains("12345").endsWith("</script>");
  }

  @Test
  public void skip_google_analytics_in_dev_mode() throws IOException {
    Env env = mock(Env.class);
    when(env.prodMode()).thenReturn(false);

    compiler.configure(hb -> hb.registerHelpers(new GoogleAnalyticsHelper("ID")));

    String result = compile("[[google_analytics UA]]", map("env", env));

    assertThat(result).isEmpty();
  }

  @Test
  public void can_override_helper() throws IOException {
    compiler.configure(hb -> hb.registerHelpers(new GoogleAnalyticsHelper("DEFAULT_ID")));
    compiler.configure(hb -> hb.registerHelpers(new GoogleAnalyticsHelper("OVERRIDEN")));

    String result = compile("[[google_analytics]]", new TreeMap<>());

    assertThat(result).contains("OVERRIDEN").doesNotContain("DEFAULT_ID");
  }

  @Test
  public void compatibility_with_templating() {
    assertThat(compiler.supports(Paths.get("index.html"))).isTrue();
    assertThat(compiler.supports(Paths.get("data.xml"))).isTrue();
    assertThat(compiler.supports(Paths.get("data.json"))).isTrue();
    assertThat(compiler.supports(Paths.get("test.md"))).isTrue();
    assertThat(compiler.supports(Paths.get("test.markdown"))).isTrue();
    assertThat(compiler.supports(Paths.get("text.txt"))).isTrue();
    assertThat(compiler.supports(Paths.get("style.css.map"))).isFalse();
    assertThat(compiler.supports(Paths.get("style.css"))).isFalse();
    assertThat(compiler.supports(Paths.get("style.less"))).isFalse();
    assertThat(compiler.supports(Paths.get("text.zip"))).isFalse();
    assertThat(compiler.supports(Paths.get("text.gz"))).isFalse();
    assertThat(compiler.supports(Paths.get("text.pdf"))).isFalse();
    assertThat(compiler.supports(Paths.get("image.gif"))).isFalse();
    assertThat(compiler.supports(Paths.get("image.jpeg"))).isFalse();
    assertThat(compiler.supports(Paths.get("image.jpg"))).isFalse();
    assertThat(compiler.supports(Paths.get("image.png"))).isFalse();
    assertThat(compiler.supports(Paths.get("font.svg"))).isFalse();
    assertThat(compiler.supports(Paths.get("font.eot"))).isFalse();
    assertThat(compiler.supports(Paths.get("font.ttf"))).isFalse();
    assertThat(compiler.supports(Paths.get("font.woff"))).isFalse();
    assertThat(compiler.supports(Paths.get("font.woff2"))).isFalse();
    assertThat(compiler.supports(Paths.get("script.js"))).isFalse();
    assertThat(compiler.supports(Paths.get("script.coffee"))).isFalse();
    assertThat(compiler.supports(Paths.get("script.litcoffee"))).isFalse();
    assertThat(compiler.supports(Paths.get("favicon.ico"))).isFalse();
    assertThat(compiler.supports(Paths.get("unknown"))).isFalse();
  }

  private static Map<String, Object> map(String key, Object value) {
    return new TreeMap<String, Object>() {{
      put(key, value);
    }};
  }

  private static Map<String, Object> map(String key1, Object value1, String key2, Object value2) {
    return new TreeMap<String, Object>() {{
      put(key1, value1);
      put(key2, value2);
    }};
  }

  public static class JavaBean {
    private final String name;
    public final int age;

    private JavaBean(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public String getFullDescription() {
      return name + "-" + age;
    }

    public String description() {
      return name;
    }
  }
}
