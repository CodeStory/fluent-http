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
  public void unwrap() {
    assertThat(simpleQuery.unwrap(Query.class)).isSameAs(query);
  }
}