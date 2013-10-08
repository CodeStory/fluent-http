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

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HandlebarsCompilerTest {
  HandlebarsCompiler compiler = new HandlebarsCompiler();

  @Test
  public void compile() throws IOException {
    Map<String, Object> variables = new HashMap<>();
    variables.put("greeting", "Hello");

    String result = compiler.compile("-[[greeting]]-", variables);

    assertThat(result).isEqualTo("-Hello-");
  }

  @Test
  public void partials() throws IOException {
    Map<String, Object> variables = new HashMap<>();
    variables.put("name", "Bob");

    String result = compiler.compile("-[[>partial]] [[>partial]]-", variables);

    assertThat(result).isEqualTo("-Hello Bob Hello Bob-");
  }

  @Test
  public void string_helpers() throws IOException {
    Map<String, Object> variables = new HashMap<>();
    variables.put("name", "joe");

    String result = compiler.compile("Hello [[capitalizeFirst name]]", variables);

    assertThat(result).isEqualTo("Hello Joe");
  }

  @Test
  public void java_getters() throws IOException {
    Map<String, Object> variables = new HashMap<>();
    variables.put("bean", new JavaBean("Bob", 12));

    String result = compiler.compile("[[bean.name]]", variables);

    assertThat(result).isEqualTo("Bob");
  }

  @Test
  public void java_fields() throws IOException {
    Map<String, Object> variables = new HashMap<>();
    variables.put("bean", new JavaBean("Bob", 12));

    String result = compiler.compile("[[bean.age]]", variables);

    assertThat(result).isEqualTo("12");
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
  }
}
