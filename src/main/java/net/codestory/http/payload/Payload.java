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
import static java.time.format.DateTimeFormatter.*;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

import net.codestory.http.compilers.Compiler;
import net.codestory.http.convert.*;
import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

import org.simpleframework.http.*;

public class Payload {
  private final String contentType;
  private final Object content;
  private int code;
  private final Map<String, String> headers;
  private final List<Cookie> cookies;

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
    if (content instanceof Payload) {
      Payload wrapped = (Payload) content;
      this.contentType = (null == contentType) ? wrapped.contentType : contentType;
      this.content = wrapped.content;
      this.code = wrapped.code;
      this.headers = new LinkedHashMap<>(wrapped.headers);
      this.cookies = new ArrayList<>(wrapped.cookies);
    } else {
      this.contentType = contentType;
      this.content = content;
      this.code = code;
      this.headers = new LinkedHashMap<>();
      this.cookies = new ArrayList<>();
    }
  }

  public Payload withHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public Payload withHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
    return this;
  }

  public Payload withCookie(String name, int value) {
    return withCookie(name, Integer.toString(value));
  }

  public Payload withCookie(String name, long value) {
    return withCookie(name, Long.toString(value));
  }

  public Payload withCookie(String name, boolean value) {
    return withCookie(name, Boolean.toString(value));
  }

  public Payload withCookie(String name, String value) {
    return withCookie(new Cookie(name, value, "/", true));
  }

  public Payload withCookie(Cookie cookie) {
    cookies.add(cookie);
    return this;
  }

  public Payload withCookies(List<Cookie> cookies) {
    cookies.addAll(cookies);
    return this;
  }

  public Payload withCode(int code) {
    this.code = code;
    return this;
  }

  public String rawContentType() {
    return contentType;
  }

  public Object rawContent() {
    return content;
  }

  public Map<String, String> headers() {
    return headers;
  }

  public List<Cookie> cookies() {
    return cookies;
  }

  public int code() {
    return code;
  }

  public boolean isSuccess() {
    return (code >= 200) && (code <= 299);
  }

  public boolean isError() {
    return (code >= 400) && (code <= 599);
  }

  public static Payload ok() {
    return new Payload(200);
  }

  public static Payload created() {
    return new Payload(201);
  }

  public static Payload movedPermanently(String url) {
    return new Payload(301).withHeader("Location", url);
  }

  public static Payload seeOther(String uri) {
    return new Payload(303).withHeader("Location", uri);
  }

  public static Payload seeOther(URI uri) {
    return seeOther(uri.toString());
  }

  public static Payload temporaryRedirect(String uri) {
    return new Payload(307).withHeader("Location", uri);
  }

  public static Payload temporaryRedirect(URI uri) {
    return temporaryRedirect(uri.toString());
  }

  public static Payload notModified() {
    return new Payload(304);
  }

  public static Payload unauthorized(String realm) {
    return new Payload(401).withHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
  }

  public static Payload forbidden() {
    return new Payload(403);
  }

  public static Payload notFound() {
    return new Payload(404);
  }

  public static Payload methodNotAllowed() {
    return new Payload(405);
  }

  public boolean isBetter(Payload other) {
    // WTF?
    if (200 == code) {
      return other.code() != 200;
    }
    if (405 == code) {
      return (other.code() != 200) && (other.code() != 405);
    }
    if (303 == code) {
      return (other.code() != 200) && (other.code() != 405) && (other.code() != 303);
    }
    return false;
  }

  public void writeTo(Context context) throws IOException {
    Response response = context.response();

    headers.entrySet().forEach(entry -> response.setValue(entry.getKey(), entry.getValue()));
    addHeadersForContent(response);
    cookies.forEach(cookie -> response.setCookie(cookie));
    response.setStatus(Status.getStatus(code));

    String uri = context.uri();
    byte[] data = getData(uri);
    if (data != null) {
      response.setValue("Content-Type", getContentType(uri));
      response.setContentLength(data.length);
      response.getOutputStream().write(data);
    } else {
      response.setContentLength(0);
    }
  }

  public String getContentType(String uri) {
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
    if (content instanceof String) {
      return "text/html;charset=UTF-8";
    }
    if (content instanceof InputStream) {
      return "application/octet-stream";
    }
    if (content instanceof ModelAndView) {
      Path path = Resources.findExistingPath(((ModelAndView) content).view());
      return ContentTypes.get(path);
    }
    if (content instanceof Model) {
      Path path = Resources.findExistingPath(uri);
      return ContentTypes.get(path);
    }
    return "application/json;charset=UTF-8";
  }

  public byte[] getData(String uri) throws IOException {
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
    if (content instanceof ModelAndView) {
      return forModelAndView((ModelAndView) content);
    }
    if (content instanceof Model) {
      return forModelAndView(ModelAndView.of(uri, (Model) content));
    }

    return TypeConvert.toByteArray(content);
  }

  private void addHeadersForContent(Response response) {
    if (content instanceof Path) {
      addLastModifiedHeader(((Path) content).toFile(), response);
    } else if (content instanceof File) {
      addLastModifiedHeader((File) content, response);
    }
  }

  private void addLastModifiedHeader(File file, Response response) {
    response.setValue("Last-Modified", RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneOffset.systemDefault())));
  }

  private static byte[] forString(String value) {
    return value.getBytes(UTF_8);
  }

  private static byte[] forInputStream(InputStream stream) throws IOException {
    return InputStreams.readBytes(stream);
  }

  private static byte[] forModelAndView(ModelAndView modelAndView) {
    return forString(new Template(modelAndView.view()).render(modelAndView.model()));
  }

  private static byte[] forPath(Path path) throws IOException {
    if (ContentTypes.is_binary(path)) {
      return Resources.readBytes(path);
    }

    if (ContentTypes.support_templating(path)) {
      return forModelAndView(ModelAndView.of(path.toString()));
    }

    String content = Resources.read(path, UTF_8);
    String compiled = Compiler.compile(path, content);
    return forString(compiled);
  }
}
