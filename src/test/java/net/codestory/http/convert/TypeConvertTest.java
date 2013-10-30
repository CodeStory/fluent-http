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
package net.codestory.http.convert;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import org.junit.*;

public class TypeConvertTest {
  @Test
  public void dont_convert() {
    assertThat(TypeConvert.convert("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(TypeConvert.convert("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void convert_integer() {
    assertThat(TypeConvert.convert("42", Integer.class)).isEqualTo(42);
    assertThat(TypeConvert.convert("42", int.class)).isEqualTo(42);
  }

  @Test
  public void convert_boolean() {
    assertThat(TypeConvert.convert("true", Boolean.class)).isEqualTo(true);
    assertThat(TypeConvert.convert("true", boolean.class)).isEqualTo(true);
    assertThat(TypeConvert.convert("false", Boolean.class)).isEqualTo(false);
    assertThat(TypeConvert.convert("false", boolean.class)).isEqualTo(false);
  }

  @Test
  public void convert_key_values() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("name", "joe");
    keyValues.put("age", "42");

    Human human = TypeConvert.convert(keyValues, Human.class);

    assertThat(human.name).isEqualTo("joe");
    assertThat(human.age).isEqualTo(42);
  }

  static class Human {
    public String name;
    public int age;
  }
}
