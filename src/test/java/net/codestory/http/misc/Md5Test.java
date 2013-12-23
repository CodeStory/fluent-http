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
package net.codestory.http.misc;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.*;

import org.junit.*;

public class Md5Test {
  @Test
  public void md5() {
    assertThat(Md5.of("".getBytes(StandardCharsets.UTF_8))).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    assertThat(Md5.of("Hello".getBytes(StandardCharsets.UTF_8))).isEqualTo("8b1a9953c4611296a827abf8c47804d7");
  }
}