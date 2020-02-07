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

import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SiteTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();
  static Env env = Env.prod();
  static Site site = new Site(env, new Resources(env));

  @Test
  public void pages() {
    List<Map<String, Object>> pages = site.getPages();

    assertThat(pages).hasSize(35);
  }

  @Test
  public void pages_with_directory_symlink() throws IOException {
    folder.newFolder("app", "target_folder").toPath().resolve("file.txt").toFile().createNewFile();
    Files.createSymbolicLink(
      folder.getRoot().toPath().resolve("app").resolve("symlink"),
      folder.getRoot().toPath().resolve("app").resolve("target_folder")
    );
    Env dev = Env.dev(folder.getRoot());

    List<Map<String, Object>> pages = new Site(dev, new Resources(dev)).getPages();

    assertThat(pages).hasSize(1);
  }

  @Test
  public void tags() {
    Map<String, List<Map<String, Object>>> tags = site.getTags();

    assertThat(tags).hasSize(3);
    assertThat(tags.get("")).hasSize(33);
    assertThat(tags.get("scala")).hasSize(2);
    assertThat(tags.get("java")).hasSize(1);
  }

  @Test
  public void categories() {
    Map<String, List<Map<String, Object>>> categories = site.getCategories();

    assertThat(categories).hasSize(3);
    assertThat(categories.get("")).hasSize(33);
    assertThat(categories.get("post")).hasSize(1);
    assertThat(categories.get("test")).hasSize(1);
  }

  @Test
  public void data_folder() {
    Map<String, Object> data = site.getData();

    assertThat(data).hasSize(2);
    assertThat((List) data.get("members")).hasSize(3);
    assertThat((List) data.get("products")).hasSize(2);
  }

  @Test
  public void get_as_bean() {
    Config config = site.getAs("config", Config.class);

    assertThat(config.greeting).isEqualTo("Hello");
    assertThat(config.name).isEqualTo("Bob");
  }

  private static class Config {
    public String greeting;
    public String name;
  }
}
