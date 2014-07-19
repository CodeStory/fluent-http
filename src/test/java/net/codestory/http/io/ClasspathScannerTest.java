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
package net.codestory.http.io;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.*;
import java.util.*;

import net.codestory.http.misc.*;

import org.junit.*;

public class ClasspathScannerTest {
  ClasspathScanner classpathScanner = new ClasspathScanner();
  Env env = new Env();

  @Test
  public void scan_main_resources() {
    Set<String> resources = classpathScanner.getResources(env.appPath());

    assertThat(resources)
      .contains("app/404.html")
      .contains("app/500.html")
      .contains("app/_layouts/default.html");
  }

  @Test
  public void scan_test_resources() {
    Set<String> resources = classpathScanner.getResources(env.appPath());

    assertThat(resources)
      .contains("app/0variable.txt")
      .contains("app/assets/style.css")
      .contains("app/_config.yml");
  }

  @Test
  public void scan_webjars() {
    Set<String> resources = classpathScanner.getResources(Paths.get("META-INF/resources/webjars/"));

    assertThat(resources)
      .contains("META-INF/resources/webjars/fakewebjar/1.0/fake.js")
      .contains("META-INF/resources/webjars/jquery/1.11.1/jquery.js");
  }
}
