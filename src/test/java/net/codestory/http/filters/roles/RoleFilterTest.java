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
package net.codestory.http.filters.roles;

import static net.codestory.http.constants.HttpStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import net.codestory.http.exchange.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

import org.junit.*;

public class RoleFilterTest {
  Context context = mock(Context.class);
  PayloadSupplier nextFilter = mock(PayloadSupplier.class);
  Payload nextPayload = mock(Payload.class);
  User user = mock(User.class);

  RoleFilter filter;

  @Before
  public void setUp() {
    filter = new RoleFilter(new HashMap<String, String>() {{
      put("/", "USER");
      put("/private", "ADMIN");
    }});
  }

  @Test
  public void no_user() throws IOException {
    when(context.currentUser()).thenReturn(null);
    when(nextFilter.get()).thenReturn(nextPayload);

    Payload payload = filter.apply("/", context, nextFilter);

    assertThat(payload).isSameAs(nextPayload);
  }

  @Test
  public void authorized() throws IOException {
    when(context.currentUser()).thenReturn(user);
    when(user.isInRole("USER")).thenReturn(true);
    when(nextFilter.get()).thenReturn(nextPayload);

    Payload payload = filter.apply("/", context, nextFilter);

    assertThat(payload).isSameAs(nextPayload);
  }

  @Test
  public void not_authorized() throws IOException {
    when(context.currentUser()).thenReturn(user);
    when(user.isInRole("USER")).thenReturn(false);

    Payload payload = filter.apply("/", context, nextFilter);

    assertThat(payload.code()).isEqualTo(FORBIDDEN);
  }

  @Test
  public void authorized_admin() throws IOException {
    when(context.currentUser()).thenReturn(user);
    when(user.isInRole("ADMIN")).thenReturn(true);
    when(nextFilter.get()).thenReturn(nextPayload);

    Payload payload = filter.apply("/private/section", context, nextFilter);

    assertThat(payload).isSameAs(nextPayload);
  }

  @Test
  public void not_authorized_admin() throws IOException {
    when(context.currentUser()).thenReturn(user);
    when(user.isInRole("USER")).thenReturn(true);
    when(user.isInRole("ADMIN")).thenReturn(false);

    Payload payload = filter.apply("/private/section", context, nextFilter);

    assertThat(payload.code()).isEqualTo(FORBIDDEN);
  }
}
