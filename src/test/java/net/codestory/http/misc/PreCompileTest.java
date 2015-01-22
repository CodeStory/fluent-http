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

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PreCompileTest {
  @Rule
  public TemporaryFolder source = new TemporaryFolder();

  @Rule
  public TemporaryFolder target = new TemporaryFolder();

  @Test
  public void preCompile() throws IOException {
    Env prod = new Env(source.getRoot(), true, true, false, false);

    PreCompile preCompile = new PreCompile(prod);
    preCompile.run(target.getRoot().getAbsolutePath());
  }
}