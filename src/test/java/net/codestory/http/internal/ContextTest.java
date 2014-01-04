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
package net.codestory.http.internal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.*;
import org.simpleframework.http.*;

public class ContextTest {
  Request request = mock(Request.class);
  Response response = mock(Response.class);

  Context context = new Context(request, response);

  @Test
  public void missing_cookie() {
    String value = context.cookieValue("name");

    assertThat(value).isNull();
  }

  @Test
  public void default_value() {
    String value = context.cookieValue("name", "default");

    assertThat(value).isEqualTo("default");
  }

  @Test
  public void cookie_value() {
    when(request.getCookie("name")).thenReturn(new Cookie("name", "value"));

    String value = context.cookieValue("name", "default");

    assertThat(value).isEqualTo("value");
  }

  @Test
  public void json_cookie_json_by_type() throws IOException {
    when(request.getCookie("name")).thenReturn(new Cookie("name", "{\"name\": \"Bob\", \"quantity\": 42}"));

    Order order = context.cookieValue("name", Order.class);

    assertThat(order.name).isEqualTo("Bob");
    assertThat(order.quantity).isEqualTo(42);
  }

  @Test
  public void json_cookie_default_value() throws IOException {
    Order order = context.cookieValue("name", new Order());

    assertThat(order.name).isNull();
    assertThat(order.quantity).isZero();
  }

  @Test
  public void json_cookie() throws IOException {
    when(request.getCookie("name")).thenReturn(new Cookie("name", "{\"name\": \"Joe\", \"quantity\": 12}"));

    Order order = context.cookieValue("name", new Order());

    assertThat(order.name).isEqualTo("Joe");
    assertThat(order.quantity).isEqualTo(12);
  }

  static class Order {
    String name;
    int quantity;
  }
}
