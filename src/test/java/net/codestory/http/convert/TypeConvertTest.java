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

import java.time.*;
import java.util.*;

import org.junit.*;

public class TypeConvertTest {
  @Test
  public void dont_convert() {
    assertThat(TypeConvert.convertValue("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(TypeConvert.convertValue("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void to_integer() {
    assertThat(TypeConvert.convertValue("42", Integer.class)).isEqualTo(42);
    assertThat(TypeConvert.convertValue("42", int.class)).isEqualTo(42);
  }

  @Test
  public void to_boolean() {
    assertThat(TypeConvert.convertValue("true", Boolean.class)).isTrue();
    assertThat(TypeConvert.convertValue("true", boolean.class)).isTrue();
    assertThat(TypeConvert.convertValue("false", Boolean.class)).isFalse();
    assertThat(TypeConvert.convertValue("false", boolean.class)).isFalse();
  }

  @Test
  public void to_bean() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("name", "joe");
    keyValues.put("age", "42");

    Human human = TypeConvert.convertValue(keyValues, Human.class);

    assertThat(human.name).isEqualTo("joe");
    assertThat(human.age).isEqualTo(42);
  }

  @Test
  public void ignore_missing_fields() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("name", "joe");
    keyValues.put("city", "Paris");

    Human human = TypeConvert.convertValue(keyValues, Human.class);

    assertThat(human.name).isEqualTo("joe");
    assertThat(human.age).isZero();
  }

  @Test
  public void json_to_bean() {
    Human human = TypeConvert.fromJson("{\"name\":\"jack\",\"age\":31,\"birthDate\":\"1980-01-01\"}", Human.class);

    assertThat(human.name).isEqualTo("jack");
    assertThat(human.age).isEqualTo(31);
    assertThat(human.birthDate).isEqualTo(LocalDate.parse("1980-01-01"));
  }

  static class Human {
    String name;
    int age;
    LocalDate birthDate;
  }
}
