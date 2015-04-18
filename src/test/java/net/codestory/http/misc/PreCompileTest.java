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
package net.codestory.http.misc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class PreCompileTest {
  @Rule
  public TemporaryFolder source = new TemporaryFolder();

  @Rule
  public TemporaryFolder target = new TemporaryFolder();

  @Test
  public void preCompile() throws IOException {
    File app = source.newFolder("app");
    File coffee = new File(app, "test.coffee");
    Files.write("console.log 'Hello'", coffee, UTF_8);
    File less = new File(app, "test.less");
    Files.write("body{h1{color:red}}", less, UTF_8);

    Env prod = Env.prod().withWorkingDir(source.getRoot()).withClassPath(false);
    PreCompile preCompile = new PreCompile(prod);
    preCompile.run(target.getRoot().getAbsolutePath());

    assertThat(new File(target.getRoot(), "test.js")).hasContent("console.log('Hello');");
    assertThat(new File(target.getRoot(), "test.css")).hasContent("body h1 {\n  color: red;\n}");
  }
}