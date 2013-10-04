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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;

import org.junit.*;

import com.google.common.collect.*;

public class MustacheCompilerTest {
  MustacheCompiler compiler = new MustacheCompiler();

  @Test
  public void compile() throws IOException {
    String result = compiler.compile("-[[greeting]]-", ImmutableMap.<String, Object>of("greeting", "Hello"));

    assertThat(result).isEqualTo("-Hello-");
  }

  @Test
  public void partials() throws IOException {
    String result = compiler.compile("-[[>partial]] [[>partial]]-", ImmutableMap.<String, Object>of("name", "Bob"));

    assertThat(result).isEqualTo("-Hello Bob Hello Bob-");
  }
}
