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

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public class HandlebarsCompilerTest {
  HandlebarsCompiler compiler = new HandlebarsCompiler();

  @Test
  public void compile() throws IOException {
    String result = compiler.compile("-[[greeting]]-", map("greeting", "Hello"));

    assertThat(result).isEqualTo("-Hello-");
  }

  @Test
  public void partials() throws IOException {
    String result = compiler.compile("-[[>partial.txt]] [[>partial.txt]]-", map("name", "Bob"));

    assertThat(result).isEqualTo("-Hello Bob Hello Bob-");
  }

  @Test
  public void find_partial() throws IOException {
    String result = compiler.compile("[[>partial]]", map("name", "Bob"));

    assertThat(result).isEqualTo("Hello Bob");
  }

  @Test
  public void string_helpers() throws IOException {
    String result = compiler.compile("Hello [[capitalizeFirst name]]", map("name", "joe"));

    assertThat(result).isEqualTo("Hello Joe");
  }

  @Test
  public void java_getters_and_fields() throws IOException {
    String result = compiler.compile("[[bean.name]] is [[bean.age]]", map("bean", new JavaBean("Bob", 12)));

    assertThat(result).isEqualTo("Bob is 12");
  }

  @Test
  public void java_method() throws IOException {
    String result = compiler.compile("[[bean.fullDescription]]", map("bean", new JavaBean("Bob", 12)));

    assertThat(result).isEqualTo("Bob-12");
  }

  @Test
  public void each() throws IOException {
    String result = compiler.compile("[[#each list]][[.]][[/each]]", map("list", asList("A", "B")));

    assertThat(result).isEqualTo("AB");
  }

  @Test
  public void each_reverse() throws IOException {
    String result = compiler.compile("[[#each_reverse list]][[.]][[/each_reverse]]", map("list", asList("A", "B")));

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

    String result = compiler.compile("[[#each_value descriptions letters]][[@key]]=[[.]][[/each_value]]", variables);

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

    String result = compiler.compile("[[#each_value descriptions letters]][[@value.id]]=[[.]][[/each_value]]", variables);

    assertThat(result).isEqualTo("idA=Description AidB=Description B");
  }

  private static Map<String, Object> map(String key, Object value) {
    return new TreeMap<String, Object>() {{
      put(key, value);
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
  }
}
