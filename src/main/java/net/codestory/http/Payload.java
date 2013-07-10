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

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

import com.google.gson.*;
import com.sun.net.httpserver.*;

public class Payload {
  private final String contentType;
  private final Object content;
  private final int code;
  private final Headers headers;

  public Payload(Object content) {
    this(null, content);
  }

  public Payload(String contentType, Object content) {
    this(contentType, content, 200);
  }

  public Payload(String contentType, Object content, int code) {
    this.contentType = contentType;
    this.content = content;
    this.code = code;
    this.headers = new Headers();
  }

  public static Payload wrap(Object payload) {
    return (payload instanceof Payload) ? (Payload) payload : new Payload(payload);
  }

  public static Payload seeOther(String url) {
    Payload payload = new Payload(null, null, 303);
    payload.headers.add("Location", url);
    return payload;
  }

  public int code() {
    return code;
  }

  public void writeTo(HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().putAll(headers);

    byte[] data = getData();
    if (data != null) {
      exchange.getResponseHeaders().add("Content-Type", getContentType());
      exchange.sendResponseHeaders(code, data.length);
      exchange.getResponseBody().write(data);
    } else {
      exchange.sendResponseHeaders(code, 0);
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
    return forString(new Gson().toJson(content));
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

    String content = new Template(path).render();
    if (!ContentTypes.support_templating(path)) {
      content = Resources.read(path, StandardCharsets.UTF_8);
    }

    if (path.toString().endsWith(".less")) {
      return forString(new LessCompiler().compile(content));
    }
    if (path.toString().endsWith(".coffee")) {
      return forString(new CoffeeScriptCompiler().compile(content));
    }

    return forString(content);
  }
}
