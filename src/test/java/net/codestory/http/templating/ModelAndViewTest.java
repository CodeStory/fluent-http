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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Test;

public class ModelAndViewTest {
  @Test
  public void empty() {
    ModelAndView modelAndView = ModelAndView.of("empty");

    assertThat(modelAndView.view()).isEqualTo("empty");
    assertThat(modelAndView.model().keyValues()).isEmpty();
  }

  @Test
  public void singleton() {
    ModelAndView modelAndView = ModelAndView.of("view", "key", "value");

    assertThat(modelAndView.view()).isEqualTo("view");
    assertThat(modelAndView.get("key")).isEqualTo("value");
    assertThat(modelAndView.model().keyValues()).containsExactly(entry("key", "value"));
  }

  @Test
  public void two_keys() {
    ModelAndView modelAndView = ModelAndView.of("view", "key1", "value1", "key2", "value2");

    assertThat(modelAndView.view()).isEqualTo("view");
    assertThat(modelAndView.model().keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"));
  }

  @Test
  public void three_keys() {
    ModelAndView modelAndView = ModelAndView.of("view", "key1", "value1", "key2", "value2", "key3", "value3");

    assertThat(modelAndView.view()).isEqualTo("view");
    assertThat(modelAndView.model().keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"), entry("key3", "value3"));
  }

  @Test
  public void four_keys() {
    ModelAndView modelAndView = ModelAndView.of("view", "key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");

    assertThat(modelAndView.view()).isEqualTo("view");
    assertThat(modelAndView.model().keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"), entry("key3", "value3"), entry("key4", "value4"));
  }
}