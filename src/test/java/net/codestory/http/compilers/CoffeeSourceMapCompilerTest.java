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
package net.codestory.http.compilers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


public class CoffeeSourceMapCompilerTest {

  @Test
  public void generate_sourcemapfile_with_filename_and_sources() throws IOException {
    String result = new CoffeeSourceMapCompiler().compile(Paths.get("polka.coffee"), "a=b=3\nc=4\nd=(a+b)*c");
    assertThat(result).isEqualTo(Resources.toString(Resources.getResource("polka.coffee.map"), Charsets.UTF_8));
  }

}