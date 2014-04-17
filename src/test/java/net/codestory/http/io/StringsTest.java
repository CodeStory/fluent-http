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

import org.junit.*;

public class StringsTest {
  @Test
  public void count_matches() {
    assertThat(Strings.countMatches("123456789", "0")).isZero();
    assertThat(Strings.countMatches("123456789", "1")).isEqualTo(1);
    assertThat(Strings.countMatches("111111111", "1")).isEqualTo(9);
  }

  @Test
  public void strip_quotes() {
    assertThat(Strings.stripQuotes(null)).isNull();
    assertThat(Strings.stripQuotes("")).isEmpty();
    assertThat(Strings.stripQuotes("TEXT")).isEqualTo("TEXT");
    assertThat(Strings.stripQuotes("\"\"")).isEmpty();
    assertThat(Strings.stripQuotes("\"TEXT\"")).isEqualTo("TEXT");
  }

  @Test
  public void substring_before_last() {
    assertThat(Strings.substringBeforeLast("", "")).isEmpty();
    assertThat(Strings.substringBeforeLast("name.jar", ".")).isEqualTo("name");
    assertThat(Strings.substringBeforeLast("name.jar", ".jar")).isEqualTo("name");
    assertThat(Strings.substringBeforeLast("name.jar", "name.jar")).isEmpty();
    assertThat(Strings.substringBeforeLast("name.jar", "unknown")).isEqualTo("name.jar");
  }

  @Test
  public void substring_after() {
    assertThat(Strings.substringAfter("", "")).isEmpty();
    assertThat(Strings.substringAfter("name.jar", ".")).isEqualTo("jar");
    assertThat(Strings.substringAfter("name.jar", ".jar")).isEmpty();
    assertThat(Strings.substringAfter("name.jar", "name.jar")).isEmpty();
    assertThat(Strings.substringAfter("name.jar", "unknown")).isEmpty();
  }
}
