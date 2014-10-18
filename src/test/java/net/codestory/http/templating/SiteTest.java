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

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.codestory.http.misc.*;

import org.junit.*;

public class SiteTest {
  private static Site site = new Site(new Env());

  @Test
  public void pages() {
    List<Map<String, Object>> pages = site.getPages();

    assertThat(pages).hasSize(30);
  }

  @Test
  public void tags() {
    Map<String, List<Map<String, Object>>> tags = site.getTags();

    assertThat(tags).hasSize(3);
    assertThat(tags.get("")).hasSize(28);
    assertThat(tags.get("scala")).hasSize(2);
    assertThat(tags.get("java")).hasSize(1);
  }

  @Test
  public void categories() {
    Map<String, List<Map<String, Object>>> categories = site.getCategories();

    assertThat(categories).hasSize(3);
    assertThat(categories.get("")).hasSize(28);
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
