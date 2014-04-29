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

import static net.codestory.http.constants.Headers.*;

import java.io.*;
import java.util.*;

import net.codestory.http.convert.*;
import net.codestory.http.injection.*;
import net.codestory.http.io.*;
import net.codestory.http.security.*;

import org.simpleframework.http.*;

public class Context {
  private final Request request;
  private final Response response;
  private final IocAdapter iocAdapter;
  private final Query query;
  private User currentUser;

  public Context(Request request, Response response, IocAdapter iocAdapter) {
    this.request = request;
    this.response = response;
    this.iocAdapter = iocAdapter;
    this.query = request.getQuery();
  }

  public String uri() {
    return request.getPath().getPath();
  }

  public Cookie cookie(String name) {
    return request.getCookie(name);
  }

  public String cookieValue(String name) {
    Cookie cookie = cookie(name);
    return (cookie == null) ? null : cookie.getValue();
  }

  @SuppressWarnings("unchecked")
  public <T> T cookieValue(String name, T defaultValue) {
    T value = cookieValue(name, (Class<T>) defaultValue.getClass());
    return (value == null) ? defaultValue : value;
  }

  @SuppressWarnings("unchecked")
  public <T> T cookieValue(String name, Class<T> type) {
    String value = cookieValue(name);
    return (value == null) ? null : TypeConvert.fromJson(value, type);
  }

  public String cookieValue(String name, String defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : value;
  }

  public int cookieValue(String name, int defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }

  public long cookieValue(String name, long defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Long.parseLong(value);
  }

  public boolean cookieValue(String name, boolean defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }

  public List<Cookie> cookies() {
    return request.getCookies();
  }

  public String get(String name) {
    return query.get(name);
  }

  public List<String> getAll(String name) {
    return query.getAll(name);
  }

  public int getInteger(String name) {
    return query.getInteger(name);
  }

  public float getFloat(String name) {
    return query.getFloat(name);
  }

  public boolean getBoolean(String name) {
    return query.getBoolean(name);
  }

  public String getHeader(String name) {
    return request.getValue(name);
  }

  public List<String> getHeaders(String name) {
    return request.getValues(name);
  }

  public String method() {
    return request.getMethod();
  }

  public Map<String, String> keyValues() {
    return query;
  }

  public String getClientAddress() {
    String forwarded = getHeader(X_FORWARDED_FOR);
    return (forwarded != null) ? forwarded : request.getClientAddress().toString();
  }

  public Request request() {
    return request;
  }

  public Response response() {
    return response;
  }

  public byte[] content() {
    try {
      return InputStreams.readBytes(request.getInputStream());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public String contentAsString() {
    try {
      return request.getContent();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public <T> T contentAs(Class<T> type) {
    try {
      String json = request.getContent();

      return TypeConvert.fromJson(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable read request content", e);
    }
  }

  public <T> T getBean(Class<T> type) {
    return iocAdapter.get(type);
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public User currentUser() {
    return currentUser;
  }

  public boolean isUrlEncodedForm() {
    String contentType = getHeader("Content-Type");
    return (contentType != null) && (contentType.contains("application/x-www-form-urlencoded"));
  }

  @SuppressWarnings("unchecked")
  public <T> T extract(Class<T> type) {
    if (type.isAssignableFrom(Context.class)) {
      return (T) this;
    }
    if (type.isAssignableFrom(Map.class)) {
      return (T) keyValues();
    }
    if (type.isAssignableFrom(Request.class)) {
      return (T) request();
    }
    if (type.isAssignableFrom(Response.class)) {
      return (T) response();
    }
    if (type.isAssignableFrom(User.class)) {
      return (T) currentUser();
    }
    if (type.isAssignableFrom(byte[].class)) {
      return (T) content();
    }
    if (type.isAssignableFrom(String.class)) {
      return (T) contentAsString();
    }
    if (isUrlEncodedForm()) {
      return TypeConvert.convertValue(keyValues(), type);
    }

    return contentAs(type);
  }
}
