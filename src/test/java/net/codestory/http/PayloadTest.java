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
package net.codestory.http;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;
import static org.fest.assertions.MapAssert.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.*;

import com.sun.net.httpserver.*;

public class PayloadTest {
  @Test
  public void support_string() throws IOException {
    Payload payload = new Payload("Hello");

    assertThat(payload.code()).isEqualTo(200);
    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/html");
  }

  @Test
  public void support_byte_array() throws IOException {
    byte[] bytes = "Hello".getBytes(UTF_8);

    Payload payload = new Payload(bytes);

    assertThat(payload.getData()).isSameAs(bytes);
    assertThat(payload.getContentType()).isEqualTo("application/octet-stream");
  }

  @Test
  public void support_bean_to_json() throws IOException {
    Payload payload = new Payload(new Person("NAME", 42));

    assertThat(payload.getData()).isEqualTo("{\"name\":\"NAME\",\"age\":42}".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("application/json");
  }

  @Test
  public void support_custom_content_type() throws IOException {
    Payload payload = new Payload("text/plain", "Hello");

    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/plain");
  }

  @Test
  public void support_stream() throws IOException {
    Payload payload = new Payload("text/plain", new ByteArrayInputStream("Hello".getBytes()));

    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/plain");
  }

  @Test
  public void support_redirect() throws IOException {
    HttpExchange exchange = mock(HttpExchange.class);
    Headers headers = new Headers();
    when(exchange.getResponseHeaders()).thenReturn(headers);

    Payload payload = Payload.seeOther("/url");
    payload.writeTo(exchange);

    assertThat(headers).includes(entry("Location", asList("/url")));
    verify(exchange).sendResponseHeaders(303, 0);
    verifyNoMoreInteractions(ignoreStubs(exchange));
  }

  @Test
  public void support_permanent_move() throws IOException {
    HttpExchange exchange = mock(HttpExchange.class);
    Headers headers = new Headers();
    when(exchange.getResponseHeaders()).thenReturn(headers);

    Payload payload = Payload.movedPermanently("/url");
    payload.writeTo(exchange);

    assertThat(headers).includes(entry("Location", asList("/url")));
    verify(exchange).sendResponseHeaders(301, 0);
    verifyNoMoreInteractions(ignoreStubs(exchange));
  }

  static class Person {
    String name;
    int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
