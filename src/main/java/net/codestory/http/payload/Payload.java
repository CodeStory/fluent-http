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
import static net.codestory.http.constants.Encodings.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.HttpStatus.NOT_FOUND;
import static net.codestory.http.constants.Methods.*;
import static org.simpleframework.http.Status.NOT_MODIFIED;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.zip.*;

import net.codestory.http.compilers.*;
import net.codestory.http.constants.*;
import net.codestory.http.convert.*;
import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

import org.simpleframework.http.*;

public class Payload {
  private final String contentType;
  private final Object content;
  private final Map<String, String> headers;
  private final List<Cookie> cookies;
  private int code;

  public Payload(Object content) {
    this(null, content);
  }

  public Payload(String contentType, Object content) {
    this(contentType, content, HttpStatus.OK);
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
      return;
    }

    if (content instanceof Optional) {
      Optional<?> optional = (Optional<?>) content;
      if (optional.isPresent()) {
        this.content = optional.get();
        this.code = code;
      } else {
        this.content = null;
        this.code = NOT_FOUND;
      }
    } else {
      this.content = content;
      this.code = code;
    }

    this.contentType = contentType;
    this.headers = new LinkedHashMap<>();
    this.cookies = new ArrayList<>();
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

  public Payload withCookie(String name, Object value) {
    return withCookie(name, TypeConvert.toJson(value));
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
    return new Payload(HttpStatus.OK);
  }

  public static Payload created() {
    return new Payload(HttpStatus.CREATED);
  }

  public static Payload movedPermanently(String url) {
    return new Payload(HttpStatus.MOVED_PERMANENTLY).withHeader(LOCATION, url);
  }

  public static Payload seeOther(String uri) {
    return new Payload(HttpStatus.SEE_OTHER).withHeader(LOCATION, uri);
  }

  public static Payload seeOther(URI uri) {
    return seeOther(uri.toString());
  }

  public static Payload temporaryRedirect(String uri) {
    return new Payload(HttpStatus.TEMPORARY_REDIRECT).withHeader(LOCATION, uri);
  }

  public static Payload temporaryRedirect(URI uri) {
    return temporaryRedirect(uri.toString());
  }

  public static Payload notModified() {
    return new Payload(HttpStatus.NOT_MODIFIED);
  }

  public static Payload unauthorized(String realm) {
    return new Payload(HttpStatus.UNAUTHORIZED).withHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
  }

  public static Payload forbidden() {
    return new Payload(HttpStatus.FORBIDDEN);
  }

  public static Payload notFound() {
    return new Payload(NOT_FOUND);
  }

  public static Payload methodNotAllowed() {
    return new Payload(HttpStatus.METHOD_NOT_ALLOWED);
  }

  // WTF?
  public boolean isBetter(Payload other) {
    if (HttpStatus.OK == code) {
      return other.code() != HttpStatus.OK;
    }
    if (HttpStatus.METHOD_NOT_ALLOWED == code) {
      return (other.code() != HttpStatus.OK) && (other.code() != HttpStatus.METHOD_NOT_ALLOWED);
    }
    if (HttpStatus.SEE_OTHER == code) {
      return (other.code() != HttpStatus.OK) && (other.code() != HttpStatus.METHOD_NOT_ALLOWED) && (other.code() != HttpStatus.SEE_OTHER);
    }
    return false;
  }

  public void writeTo(Context context) throws IOException {
    Response response = context.response();

    headers.entrySet().forEach(entry -> response.setValue(entry.getKey(), entry.getValue()));
    addHeadersForContent(response);
    cookies.forEach(cookie -> response.setCookie(cookie));

    String uri = context.uri();
    byte[] data = getData(uri, context);
    if (data == null) {
      response.setStatus(Status.getStatus(code));
      response.setContentLength(0);
      return;
    }

    String type = getContentType(uri);
    response.setValue(CONTENT_TYPE, type);
    response.setStatus(Status.getStatus(code));

    if (HEAD.equals(context.method()) || (code == 204) || (code == 304) || ((code >= 100) && (code < 200))) {
      return;
    }

    String etag = etag(data);
    String previousEtag = context.getHeader(IF_NONE_MATCH);
    if (etag.equals(previousEtag)) {
      response.setStatus(NOT_MODIFIED);
      return;
    }
    response.setValue(ETAG, etag);

    String acceptEncoding = context.getHeader(ACCEPT_ENCODING);
    if ((acceptEncoding != null) && acceptEncoding.contains(GZIP)) {
      response.setValue(CONTENT_ENCODING, GZIP);

      GZIPOutputStream gzip = new GZIPOutputStream(response.getOutputStream());
      gzip.write(data);
      gzip.finish();
    } else {
      response.setContentLength(data.length);
      response.getOutputStream().write(data);
    }
  }

  private static String etag(byte[] data) {
    return Md5.of(data);
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

  public byte[] getData(String uri, Context context) throws IOException {
    if (content == null) {
      return null;
    }
    if (content instanceof File) {
      return forPath(((File) content).toPath(), context);
    }
    if (content instanceof Path) {
      return forPath((Path) content, context);
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
      return forModelAndView((ModelAndView) content, context);
    }
    if (content instanceof Model) {
      return forModelAndView(ModelAndView.of(uri, (Model) content), context);
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
    response.setValue(LAST_MODIFIED, RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneOffset.systemDefault())));
  }

  private static byte[] forString(String value) {
    return value.getBytes(UTF_8);
  }

  private static byte[] forInputStream(InputStream stream) throws IOException {
    return InputStreams.readBytes(stream);
  }

  private static byte[] forModelAndView(ModelAndView modelAndView, Context context) {
    String view = modelAndView.view();

    Model model = modelAndView.model();
    model = model.merge(Model.of("cookies", cookieValues(context)));

    CacheEntry html = new Template(view).render(model);

    return forString(html.getContent());
  }

  private static Map<String, String> cookieValues(Context context) {
    Map<String, String> keyValues = new HashMap<>();
    for (Cookie cookie : context.cookies()) {
      keyValues.put(cookie.getName(), cookie.getValue());
    }
    return keyValues;
  }

  private static byte[] forPath(Path path, Context context) throws IOException {
    if (ContentTypes.is_binary(path)) {
      return Resources.readBytes(path);
    }

    if (ContentTypes.support_templating(path)) {
      return forModelAndView(ModelAndView.of(path.toString()), context);
    }

    String content = Resources.read(path, UTF_8);
    CacheEntry compiled = Compilers.INSTANCE.compile(path, content);
    return forString(compiled.getContent());
  }
}
