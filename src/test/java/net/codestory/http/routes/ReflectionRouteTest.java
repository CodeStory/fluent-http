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
package net.codestory.http.routes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.codestory.http.*;

import org.junit.*;

public class ReflectionRouteTest {
  Context context = mock(Context.class);

  @Test
  public void inject_context() {
    when(context.extract(Context.class)).thenReturn(context);

    Object[] parameters = ReflectionRoute.convert(context, new String[]{"param1", "param2"}, String.class, String.class, Context.class);

    assertThat(parameters).containsExactly("param1", "param2", context);
  }

  @Test
  public void inject_request() {
    Request request = mock(Request.class);
    when(context.extract(Request.class)).thenReturn(request);

    Object[] parameters = ReflectionRoute.convert(context, new String[]{"param"}, String.class, Request.class);

    assertThat(parameters).containsExactly("param", request);
  }
}
