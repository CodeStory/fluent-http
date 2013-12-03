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

import org.junit.*;
import org.simpleframework.http.*;

public class BasicAuthFilterTest {
  private BasicAuthFilter filter;

  private Response response;
  private Request request;

  @Before
  public void init() throws IOException {
    response = mock(Response.class);
    request = mock(Request.class);

    Map<String, String> users = new HashMap<>();
    users.put("jl", "polka");
    filter = new BasicAuthFilter("/secure", "codestory", users);
  }

  @Test
  public void skip_non_secured_path() {
    assertThat(filter.apply("/foo", request, response)).isFalse();
  }

  @Test
  public void answser_401_on_non_authenticated_query() {
    assertThat(filter.apply("/secure/foo", request, response)).isTrue();
    verify(response).setStatus(Status.UNAUTHORIZED);
    verify(response).setValue("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void answer_200_on_authorized_query() {
    when(request.getValue("Authorization")).thenReturn(" Basic amw6cG9sa2E="); // "jl:polka" encoded in base64
    assertThat(filter.apply("/secure/foo", request, response)).isFalse();
  }

  @Test
  public void answer_401_on_unauthorized_query() {
    when(request.getValue("Authorization")).thenReturn(" Basic FOOBAR9sa2E=");
    assertThat(filter.apply("/secure/foo", request, response)).isTrue();
    verify(response).setStatus(Status.UNAUTHORIZED);
    verify(response).setValue("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void block_all_subsequent_paths() {
    assertThat(filter.apply("/", request, response)).isFalse();
    assertThat(filter.apply("/foo", request, response)).isFalse();
    assertThat(filter.apply("/foo/", request, response)).isFalse();
    assertThat(filter.apply("/foo/secure", request, response)).isFalse();
    assertThat(filter.apply("/secure", request, response)).isTrue();
    assertThat(filter.apply("/secure/", request, response)).isTrue();
    assertThat(filter.apply("/secure/foo", request, response)).isTrue();
    assertThat(filter.apply("/secure/foo/", request, response)).isTrue();
  }
}
