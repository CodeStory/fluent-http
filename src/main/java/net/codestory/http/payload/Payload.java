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

import java.io.*;
import java.nio.charset.*;
import java.nio.file.Path;
import java.util.*;

import net.codestory.http.compilers.Compiler;
import net.codestory.http.io.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

import org.simpleframework.http.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class Payload {
  private final String contentType;
  private final Object content;
  private final int code;
  private final Map<String, String> headers;

  public Payload(Object content) {
    this(null, content);
  }

  public Payload(String contentType, Object content) {
    this(contentType, content, 200);
  }

  public Payload(int code) {
    this(null, null, code);
  }

  public Payload(String contentType, Object content, int code) {
    this.contentType = contentType;
    this.content = content;
    this.code = code;
    this.headers = new LinkedHashMap<>();
  }

  public static Payload wrap(Object payload) {
    return (payload instanceof Payload) ? (Payload) payload : new Payload(payload);
  }

  public Payload withHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public static Payload seeOther(String url) {
    return new Payload(303).withHeader("Location", url);
  }

  public static Payload movedPermanently(String url) {
    return new Payload(301).withHeader("Location", url);
  }

  public static Payload forbidden() {
    return new Payload(403);
  }

  public int code() {
    return code;
  }

  public void writeTo(Response response) throws IOException {
    headers.entrySet().forEach(entry -> response.setValue(entry.getKey(), entry.getValue()));

    response.setCode(code);

    byte[] data = getData();
    if (data != null) {
      response.setValue("Content-Type", getContentType());
      response.setContentLength(data.length);
      response.getOutputStream().write(data);
    } else {
      response.setContentLength(0);
    }
  }

  String getContentType() {
    if (contentType != null) {
      return contentType;
    }
    if (content instanceof File) {
      File file = (File) content;
      return ContentTypes.get(file.toPath());
    }
    if (content instanceof Path) {
      Path path = (Path) content;
      return ContentTypes.get(path);
    }
    if (content instanceof byte[]) {
      return "application/octet-stream";
    }
    if (content instanceof InputStream) {
      return "application/octet-stream";
    }
    if (content instanceof String) {
      return "text/html";
    }
    return "application/json";
  }

  byte[] getData() throws IOException {
    if (content == null) {
      return null;
    }
    if (content instanceof File) {
      return forPath(((File) content).toPath());
    }
    if (content instanceof Path) {
      return forPath((Path) content);
    }
    if (content instanceof byte[]) {
      return (byte[]) content;
    }
    if (content instanceof String) {
      return forString((String) content);
    }
    if (content instanceof InputStream) {
      return forInputStream((InputStream) content);
    }

    return new ObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .writer()
        .writeValueAsBytes(content);
  }

  private static byte[] forString(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] forInputStream(InputStream stream) throws IOException {
    return InputStreams.readBytes(stream);
  }

  private static byte[] forPath(Path path) throws IOException {
    if (ContentTypes.is_binary(path)) {
      return Resources.readBytes(path);
    }

    if (!ContentTypes.support_templating(path)) {
      String content = Resources.read(path, UTF_8);

      return forString(Compiler.compile(path, content));
    }

    return forString(new Template(path).render());
  }
}
