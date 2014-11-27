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
package net.codestory.http.exchange;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;

import net.codestory.http.*;
import net.codestory.http.injection.*;
import net.codestory.http.security.*;
import net.codestory.http.templating.*;

import org.junit.*;

public class ContextTest {
  Request request = mock(Request.class);
  Response response = mock(Response.class);
  IocAdapter iocAdapter = mock(IocAdapter.class);
  Site site = mock(Site.class);

  Context context = new Context(request, response, iocAdapter, site);

  @Test
  public void create_bean() {
    Service expectedService = new Service();
    when(iocAdapter.get(Service.class)).thenReturn(expectedService);

    Service actualService = context.getBean(Service.class);

    assertThat(actualService).isSameAs(expectedService);
  }

  @Test
  public void extract() throws IOException {
    byte[] rawContent = "Content".getBytes();
    User user = mock(User.class);
    Cookies cookies = mock(Cookies.class);
    Query query = mock(Query.class);
    context.setCurrentUser(user);
    when(request.inputStream()).thenReturn(new ByteArrayInputStream(rawContent));
    when(request.content()).thenReturn("Content");
    when(request.cookies()).thenReturn(cookies);
    when(request.query()).thenReturn(query);

    assertThat(context.extract(Context.class)).isSameAs(context);
    assertThat(context.extract(Request.class)).isSameAs(request);
    assertThat(context.extract(Response.class)).isSameAs(response);
    assertThat(context.extract(User.class)).isSameAs(user);
    assertThat(context.extract(byte[].class)).isEqualTo(rawContent);
    assertThat(context.extract(String.class)).isSameAs("Content");
    assertThat(context.extract(Cookies.class)).isSameAs(cookies);
    assertThat(context.extract(Query.class)).isSameAs(query);
    assertThat(context.extract(Site.class)).isSameAs(site);
  }

  static class Service {
  }
}
