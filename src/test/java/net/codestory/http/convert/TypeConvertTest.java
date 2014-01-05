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
    assertThat(TypeConvert.fromString("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(TypeConvert.fromString("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void to_integer() {
    assertThat(TypeConvert.fromString("42", Integer.class)).isEqualTo(42);
    assertThat(TypeConvert.fromString("42", int.class)).isEqualTo(42);
  }

  @Test
  public void to_boolean() {
    assertThat(TypeConvert.fromString("true", Boolean.class)).isTrue();
    assertThat(TypeConvert.fromString("true", boolean.class)).isTrue();
    assertThat(TypeConvert.fromString("false", Boolean.class)).isFalse();
    assertThat(TypeConvert.fromString("false", boolean.class)).isFalse();
  }

  @Test
  public void to_bean() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("name", "joe");
    keyValues.put("age", "42");

    Human human = TypeConvert.fromKeyValues(keyValues, Human.class);

    assertThat(human.name).isEqualTo("joe");
    assertThat(human.age).isEqualTo(42);
  }

  @Test
  public void json_to_bean() {
    Human human = TypeConvert.fromJson("{\"name\":\"jack\",\"age\":31}", Human.class);

    assertThat(human.name).isEqualTo("jack");
    assertThat(human.age).isEqualTo(31);
  }

  static class Human {
    String name;
    int age;
  }
}
