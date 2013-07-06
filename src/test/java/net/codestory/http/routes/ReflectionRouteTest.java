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
package net.codestory.http.routes;

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class ReflectionRouteTest {
  @Test
  public void dont_convert() {
    assertThat(ReflectionRoute.convert("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(ReflectionRoute.convert("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void convert_integer() {
    assertThat(ReflectionRoute.convert("42", Integer.class)).isEqualTo(42);
    assertThat(ReflectionRoute.convert("42", int.class)).isEqualTo(42);
  }

  @Test
  public void convert_boolean() {
    assertThat(ReflectionRoute.convert("true", Boolean.class)).isEqualTo(true);
    assertThat(ReflectionRoute.convert("true", boolean.class)).isEqualTo(true);
    assertThat(ReflectionRoute.convert("false", Boolean.class)).isEqualTo(false);
    assertThat(ReflectionRoute.convert("false", boolean.class)).isEqualTo(false);
  }
}
