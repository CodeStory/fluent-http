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
package net.codestory.http.payload;

import net.codestory.http.*;
import net.codestory.http.convert.*;

import java.net.*;
import java.util.*;

import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.HttpStatus.*;

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
    this(contentType, content, OK);
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

  public Payload withMaxAge(int maxAge) {
    return withHeader(ACCESS_CONTROL_MAX_AGE, Integer.toString(maxAge));
  }

  public Payload withAllowCredentials(boolean allowedCredentials) {
    return withHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(allowedCredentials));
  }

  public Payload withAllowOrigin(String allowedOrigin) {
    return withHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
  }

  public Payload withAllowMethods(String... allowedMethods) {
    return withHeader(ACCESS_CONTROL_ALLOW_METHODS, String.join(", ", allowedMethods));
  }

  public Payload withAllowHeaders(String... allowedHeaders) {
    return withHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(", ", allowedHeaders));
  }

  public Payload withExposeHeaders(String... allowedHeaders) {
    return withHeader(ACCESS_CONTROL_EXPOSE_HEADERS, String.join(", ", allowedHeaders));
  }

  public Payload withHeader(String key, String value) {
    if (value != null) {
      headers.put(key, value);
    }
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
    return withCookie(new NewCookie(name, value, "/", true));
  }

  public Payload withCookie(String name, Object value) {
    return withCookie(name, TypeConvert.toJson(value));
  }

  public Payload withCookie(Cookie cookie) {
    cookies.add(cookie);
    return this;
  }

  public Payload withCookies(List<Cookie> cookieList) {
    cookies.addAll(cookieList);
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
    return (code >= OK) && (code <= SUCCESS_UPPER_CODE);
  }

  public boolean isError() {
    return (code >= BAD_REQUEST) && (code <= ERROR_UPPER_CODE);
  }

  public static Payload ok() {
    return new Payload(OK);
  }

  public static Payload created() {
    return new Payload(CREATED);
  }

  public static Payload created(String uri) {
    return new Payload(CREATED).withHeader(LOCATION, uri);
  }

  public static Payload movedPermanently(String uri) {
    return new Payload(MOVED_PERMANENTLY).withHeader(LOCATION, uri);
  }

  public static Payload seeOther(String uri) {
    return new Payload(SEE_OTHER).withHeader(LOCATION, uri);
  }

  public static Payload seeOther(URI uri) {
    return seeOther(uri.toString());
  }

  public static Payload temporaryRedirect(String uri) {
    return new Payload(TEMPORARY_REDIRECT).withHeader(LOCATION, uri);
  }

  public static Payload temporaryRedirect(URI uri) {
    return temporaryRedirect(uri.toString());
  }

  public static Payload notModified() {
    return new Payload(NOT_MODIFIED);
  }

  public static Payload unauthorized(String realm) {
    return new Payload(UNAUTHORIZED).withHeader(WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
  }

  public static Payload forbidden() {
    return new Payload(FORBIDDEN);
  }

  public static Payload notFound() {
    return new Payload(NOT_FOUND);
  }

  public static Payload methodNotAllowed() {
    return new Payload(METHOD_NOT_ALLOWED);
  }

  public static Payload badRequest() {
    return new Payload(BAD_REQUEST);
  }
}
