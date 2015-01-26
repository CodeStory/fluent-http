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
package net.codestory.http.templating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Test;

public class ModelTest {
  @Test
  public void empty() {
    Model model = Model.of();

    assertThat(model.keyValues()).isEmpty();
  }

  @Test
  public void singleton() {
    Model model = Model.of("key", "value");

    assertThat(model.keyValues()).containsExactly(entry("key", "value"));
    assertThat(model.get("key")).isEqualTo("value");
  }

  @Test
  public void two_keys() {
    Model model = Model.of("key1", "value1", "key2", "value2");

    assertThat(model.keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"));
  }

  @Test
  public void three_keys() {
    Model model = Model.of("key1", "value1", "key2", "value2", "key3", "value3");

    assertThat(model.keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"), entry("key3", "value3"));
  }

  @Test
  public void four_keys() {
    Model model = Model.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");

    assertThat(model.keyValues()).containsExactly(entry("key1", "value1"), entry("key2", "value2"), entry("key3", "value3"), entry("key4", "value4"));
  }
}