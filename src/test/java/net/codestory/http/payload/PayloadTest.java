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
package net.codestory.http.payload;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.internal.*;

import org.junit.*;
import org.simpleframework.http.*;

public class PayloadTest {
  @Test
  public void support_string() throws IOException {
    Payload payload = new Payload("Hello");

    assertThat(payload.code()).isEqualTo(200);
    assertThat(payload.getData("/")).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType("/")).isEqualTo("text/html;charset=UTF-8");
  }

  @Test
  public void support_byte_array() throws IOException {
    byte[] bytes = "Hello".getBytes(UTF_8);

    Payload payload = new Payload(bytes);

    assertThat(payload.getData("/")).isSameAs(bytes);
    assertThat(payload.getContentType("/")).isEqualTo("application/octet-stream");
  }

  @Test
  public void support_bean_to_json() throws IOException {
    Payload payload = new Payload(new Person("NAME", 42));

    assertThat(payload.getData("/")).isEqualTo("{\"name\":\"NAME\",\"age\":42}".getBytes(UTF_8));
    assertThat(payload.getContentType("/")).isEqualTo("application/json;charset=UTF-8");
  }

  @Test
  public void support_custom_content_type() throws IOException {
    Payload payload = new Payload("text/plain", "Hello");

    assertThat(payload.getData("/")).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType("/")).isEqualTo("text/plain");
  }

  @Test
  public void support_stream() throws IOException {
    Payload payload = new Payload("text/plain", new ByteArrayInputStream("Hello".getBytes()));

    assertThat(payload.getData("/")).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType("/")).isEqualTo("text/plain");
  }

  @Test
  public void redirect() throws IOException {
    Context context = mock(Context.class);
    Response response = mock(Response.class);
    when(context.response()).thenReturn(response);

    Payload payload = Payload.seeOther("/url");
    payload.writeTo(context);

    verify(response).setValue("Location", "/url");
    verify(response).setStatus(Status.getStatus(303));
    verify(response).setContentLength(0);
    verifyNoMoreInteractions(response);
  }

  @Test
  public void forbidden() throws IOException {
    Context context = mock(Context.class);
    Response response = mock(Response.class);
    when(context.response()).thenReturn(response);

    Payload payload = Payload.forbidden();
    payload.writeTo(context);

    verify(response).setStatus(Status.getStatus(403));
    verify(response).setContentLength(0);
    verifyNoMoreInteractions(response);
  }

  @Test
  public void permanent_move() throws IOException {
    Context context = mock(Context.class);
    Response response = mock(Response.class);
    when(context.response()).thenReturn(response);

    Payload payload = Payload.movedPermanently("/url");
    payload.writeTo(context);

    verify(response).setValue("Location", "/url");
    verify(response).setStatus(Status.getStatus(301));
    verify(response).setContentLength(0);
    verifyNoMoreInteractions(response);
  }

  @Test
  public void last_modified() throws IOException {
    Context context = mock(Context.class);
    Response response = mock(Response.class);
    when(context.response()).thenReturn(response);
    when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());

    Payload payload = new Payload(Paths.get("hello.md"));
    payload.writeTo(context);

    verify(response).setValue(eq("Last-Modified"), anyString());
  }

  @Test
  public void better() {
    Payload ok = Payload.ok();
    Payload wrongMethod = Payload.methodNotAllowed();
    Payload redirect = Payload.seeOther("/");
    Payload notFound = Payload.notFound();

    assertThat(ok.isBetter(ok)).isFalse();
    assertThat(ok.isBetter(wrongMethod)).isTrue();
    assertThat(ok.isBetter(redirect)).isTrue();
    assertThat(ok.isBetter(notFound)).isTrue();

    assertThat(wrongMethod.isBetter(ok)).isFalse();
    assertThat(wrongMethod.isBetter(wrongMethod)).isFalse();
    assertThat(wrongMethod.isBetter(redirect)).isTrue();
    assertThat(wrongMethod.isBetter(notFound)).isTrue();

    assertThat(redirect.isBetter(ok)).isFalse();
    assertThat(redirect.isBetter(wrongMethod)).isFalse();
    assertThat(redirect.isBetter(redirect)).isFalse();
    assertThat(redirect.isBetter(notFound)).isTrue();

    assertThat(notFound.isBetter(ok)).isFalse();
    assertThat(notFound.isBetter(wrongMethod)).isFalse();
    assertThat(notFound.isBetter(redirect)).isFalse();
    assertThat(notFound.isBetter(notFound)).isFalse();
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
