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
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

import org.junit.*;
import org.mockito.*;

public class BasicAuthFilterTest {
  private BasicAuthFilter filter;

  private Payload next = Payload.ok();
  private PayloadSupplier nextFilter = () -> next;
  private Context context = mock(Context.class);
  private ArgumentCaptor<User> user = forClass(User.class);

  @Before
  public void create_filter() {
    Map<String, String> users = new HashMap<>();
    users.put("jl", "polka");
    filter = new BasicAuthFilter("/secure", "codestory", users);
  }

  @Test
  public void answer_401_on_non_authenticated_query() throws IOException {
    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload.code()).isEqualTo(401);
    assertThat(payload.headers()).containsEntry("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void authorized_query() throws IOException {
    System.out.println(Base64.getEncoder().encodeToString("jl:WRONG".getBytes()));

    when(context.header("Authorization")).thenReturn("Basic amw6cG9sa2E="); // "jl:polka" encoded in base64

    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload).isSameAs(next);
    verify(context).setCurrentUser(user.capture());
    assertThat(user.getValue().login()).isEqualTo("jl");
  }

  @Test
  public void answer_401_on_invalid_password() throws IOException {
    when(context.header("Authorization")).thenReturn("Basic amw6V1JPTkc="); // "jl:WRONG" encoded in base64

    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload.code()).isEqualTo(401);
    assertThat(payload.headers()).containsEntry("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void bad_request() throws IOException {
    when(context.header("Authorization")).thenReturn("Basic INVALID");

    Payload payload = filter.apply("/secure/foo", context, nextFilter);

    assertThat(payload.code()).isEqualTo(400);
  }

  @Test
  public void block_all_subsequent_paths() {
    assertThat(filter.matches("/", null)).isFalse();
    assertThat(filter.matches("/foo", null)).isFalse();
    assertThat(filter.matches("/foo/", null)).isFalse();
    assertThat(filter.matches("/foo/secure", null)).isFalse();

    assertThat(filter.matches("/secure", null)).isTrue();
    assertThat(filter.matches("/secure/", null)).isTrue();
    assertThat(filter.matches("/secure/foo", null)).isTrue();
    assertThat(filter.matches("/secure/foo/", null)).isTrue();
  }
}
