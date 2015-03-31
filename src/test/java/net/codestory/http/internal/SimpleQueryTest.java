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
package net.codestory.http.internal;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Test;
import org.simpleframework.http.Query;

public class SimpleQueryTest {
  Query query = mock(Query.class);

  SimpleQuery simpleQuery = new SimpleQuery(query);

  @Test
  public void get() {
    when(query.get("name")).thenReturn("value");

    assertThat(simpleQuery.get("name")).isEqualTo("value");
  }

  @Test
  public void all() {
    when(query.getAll("name")).thenReturn(asList("value1", "value2"));

    assertThat(simpleQuery.all("name")).containsExactly("value1", "value2");
  }

  @Test
  public void keyValues() {
    assertThat(simpleQuery.keyValues()).isSameAs(query);
  }

  @Test
  public void keys() {
    when(query.keySet()).thenReturn(new HashSet<>(asList("key1", "key2")));

    assertThat(simpleQuery.keys()).containsExactly("key1", "key2");
  }

  @Test
  public void integer_value() {
    when(query.get("key")).thenReturn("42");

    assertThat(simpleQuery.getInteger("key")).isEqualTo(42);
    assertThat(simpleQuery.getInteger("missing")).isEqualTo(0);
    assertThat(simpleQuery.getInteger("missing", 42)).isEqualTo(42);
  }

  @Test
  public void long_value() {
    when(query.get("key")).thenReturn("42");

    assertThat(simpleQuery.getLong("key")).isEqualTo(42L);
    assertThat(simpleQuery.getLong("missing")).isEqualTo(0L);
    assertThat(simpleQuery.getLong("missing", 1337L)).isEqualTo(1337L);
  }

  @Test
  public void float_value() {
    when(query.get("key")).thenReturn("42.5");

    assertThat(simpleQuery.getFloat("key")).isEqualTo(42.5f);
    assertThat(simpleQuery.getFloat("missing")).isEqualTo(0f);
    assertThat(simpleQuery.getFloat("missing", 3.14f)).isEqualTo(3.14f);
  }

  @Test
  public void double_value() {
    when(query.get("key")).thenReturn("42.5");

    assertThat(simpleQuery.getDouble("key")).isEqualTo(42.5d);
    assertThat(simpleQuery.getDouble("missing")).isEqualTo(0d);
    assertThat(simpleQuery.getDouble("missing", 3.14d)).isEqualTo(3.14d);
  }

  @Test
  public void boolean_value() {
    when(query.get("key")).thenReturn("true");

    assertThat(simpleQuery.getBoolean("key")).isTrue();
    assertThat(simpleQuery.getBoolean("missing")).isFalse();
    assertThat(simpleQuery.getBoolean("missing", true)).isTrue();
  }

  @Test
  public void unwrap() {
    assertThat(simpleQuery.unwrap(Query.class)).isSameAs(query);
  }
}