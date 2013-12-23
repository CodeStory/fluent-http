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
package net.codestory.http.filters.etag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;

import net.codestory.http.internal.*;
import net.codestory.http.payload.*;

import org.junit.*;

public class EtagFilterTest {
  EtagFilter filter = new EtagFilter();

  Context context = mock(Context.class);

  @Test
  public void first_query() throws IOException {
    Payload payload = filter.apply("/", context, () -> new Payload("Hello"));

    assertThat(payload.code()).isEqualTo(200);
    assertThat(payload.headers().get("ETag")).isEqualTo("8b1a9953c4611296a827abf8c47804d7");
  }

  @Test
  public void not_modified() throws IOException {
    when(context.getHeader("If-None-Match")).thenReturn("8b1a9953c4611296a827abf8c47804d7");

    Payload payload = filter.apply("/", context, () -> new Payload("Hello"));

    assertThat(payload.code()).isEqualTo(302);
    assertThat(payload.headers().get("ETag")).isNull();
  }

  @Test
  public void modified() throws IOException {
    when(context.getHeader("If-None-Match")).thenReturn("OldEtag");

    Payload payload = filter.apply("/", context, () -> new Payload("Hello"));

    assertThat(payload.code()).isEqualTo(200);
    assertThat(payload.headers().get("ETag")).isEqualTo("8b1a9953c4611296a827abf8c47804d7");
  }

  @Test
  public void ignore_null_payload() throws IOException {
    Payload payload = filter.apply("/", context, () -> null);

    assertThat(payload).isNull();
  }

  @Test
  public void ignore_null_data() throws IOException {
    Payload nullData = new Payload(null);

    Payload payload = filter.apply("/", context, () -> nullData);

    assertThat(payload).isSameAs(nullData);
    assertThat(payload.headers().get("ETag")).isNull();
  }

  @Test
  public void ignore_errors() throws IOException {
    Payload notFound = Payload.notFound();

    Payload payload = filter.apply("/", context, () -> notFound);

    assertThat(payload).isSameAs(notFound);
    assertThat(payload.headers().get("ETag")).isNull();
  }

  @Test
  public void ignore_error_pages() throws IOException {
    Payload notFound = new Payload("text/html", "error", 404);

    Payload payload = filter.apply("/", context, () -> notFound);

    assertThat(payload).isSameAs(notFound);
    assertThat(payload.headers().get("ETag")).isNull();
  }
}
