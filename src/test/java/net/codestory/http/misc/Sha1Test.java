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
package net.codestory.http.misc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class Sha1Test {
  @Test
  public void sha1() {
    assertThat(Sha1.of("".getBytes(UTF_8))).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
    assertThat(Sha1.of("Hello".getBytes(UTF_8))).isEqualTo("f7ff9e8b7bb2e09b70935a5d785e0cc5d9d0abf0");
  }
}
