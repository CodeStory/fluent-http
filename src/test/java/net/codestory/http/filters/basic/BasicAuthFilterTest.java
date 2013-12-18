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
package net.codestory.http.filters.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import net.codestory.http.filters.*;
import net.codestory.http.internal.*;
import net.codestory.http.payload.*;

import org.junit.*;

public class BasicAuthFilterTest {
  private BasicAuthFilter filter;

  private Payload next = Payload.ok();
  private PayloadSupplier nextFilter = () -> next;
  private Context context;

  @Before
  public void init() throws IOException {
    context = mock(Context.class);

    Map<String, String> users = new HashMap<>();
    users.put("jl", "polka");
    filter = new BasicAuthFilter("/secure", "codestory", users);
  }

  @Test
  public void skip_non_secured_path() throws IOException {
    Payload payload = filter.apply("/foo", context, nextFilter);

    assertThat(payload).isSameAs(next);
  }

  @Test
  public void answer_401_on_non_authenticated_query() throws IOException {
    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload.code()).isEqualTo(401);
    assertThat(payload.headers()).containsEntry("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void authorized_query() throws IOException {
    when(context.get("Authorization")).thenReturn(" Basic amw6cG9sa2E="); // "jl:polka" encoded in base64

    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload).isSameAs(next);
  }

  @Test
  public void answer_401_on_unauthorized_query() throws IOException {
    when(context.get("Authorization")).thenReturn(" Basic FOOBAR9sa2E=");

    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload.code()).isEqualTo(401);
    assertThat(payload.headers()).containsEntry("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void block_all_subsequent_paths() throws IOException {
    assertThat(filter.apply("/", context, nextFilter).code()).isEqualTo(200);
    assertThat(filter.apply("/foo", context, nextFilter).code()).isEqualTo(200);
    assertThat(filter.apply("/foo/", context, nextFilter).code()).isEqualTo(200);
    assertThat(filter.apply("/foo/secure", context, nextFilter).code()).isEqualTo(200);
    assertThat(filter.apply("/secure", context, nextFilter).code()).isEqualTo(401);
    assertThat(filter.apply("/secure/", context, nextFilter).code()).isEqualTo(401);
    assertThat(filter.apply("/secure/foo", context, nextFilter).code()).isEqualTo(401);
    assertThat(filter.apply("/secure/foo/", context, nextFilter).code()).isEqualTo(401);
  }
}
