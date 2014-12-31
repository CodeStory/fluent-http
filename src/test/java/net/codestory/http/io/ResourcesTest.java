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
package net.codestory.http.io;

import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.misc.Env;
import org.junit.*;

public class ResourcesTest {
  private static Resources resources = new Resources(new Env());

  @Test
  public void exists() {
    assertThat(resources.exists(Paths.get("_layouts/default.html"))).isTrue();
    assertThat(resources.exists(Paths.get("index.html"))).isTrue();
    assertThat(resources.exists(Paths.get("/index.html"))).isTrue();
    assertThat(resources.exists(Paths.get("assets/style.css"))).isTrue();
    assertThat(resources.exists(Paths.get("js"))).isFalse();
  }

  @Test
  public void normalize_windows_path() {
    assertThat(resources.exists(Paths.get("assets\\style.css"))).isTrue();
  }

  @Test
  public void read_resource_from_sources_not_from_target_to_accelerate_feedback() {
    File file = resources.fileForClasspath(ClassPaths.getResource("app/_layouts/default.html"));

    assertThat(file.getAbsolutePath().replace('\\', '/'))
      .contains("src/main/resources/app/_layouts/default.html")
      .doesNotContain("target/classes/app/_layouts/default.html");
  }
}
