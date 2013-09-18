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
package net.codestory.http.filters;

import static net.codestory.http.routes.Match.*;
import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class MatchTest {
  @Test
  public void better() {
    assertThat(OK.isBetter(OK)).isFalse();
    assertThat(OK.isBetter(WRONG_METHOD)).isTrue();
    assertThat(OK.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_METHOD.isBetter(OK)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_URL.isBetter(OK)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_URL)).isFalse();
  }
}
