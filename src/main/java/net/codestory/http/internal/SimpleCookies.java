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

import java.util.*;

import net.codestory.http.Cookie;
import net.codestory.http.*;
import net.codestory.http.convert.*;

import org.simpleframework.http.Request;

class SimpleCookies implements Cookies {
  private final Request request;

  SimpleCookies(Request request) {
    this.request = request;
  }

  @Override
  public Iterator<Cookie> iterator() {
    return request.getCookies().stream().map(cookie -> (Cookie) new SimpleCookie(cookie)).iterator();
  }

  @Override
  public Cookie get(String name) {
    org.simpleframework.http.Cookie cookie = request.getCookie(name);
    return (cookie == null) ? null : new SimpleCookie(cookie);
  }

  @Override
  public String value(String name) {
    Cookie cookie = get(name);
    return (cookie == null) ? null : cookie.value();
  }

  @Override
  public Map<String, String> keyValues() {
    Map<String, String> keyValues = new HashMap<>();
    request.getCookies().forEach(cookie -> keyValues.put(cookie.getName(), cookie.getValue()));
    return keyValues;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T value(String name, T defaultValue) {
    T value = value(name, (Class<T>) defaultValue.getClass());
    return (value == null) ? defaultValue : value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T value(String name, Class<T> type) {
    String value = value(name);
    if (value== null) {
      return null;
    }

    // fix for https://github.com/ariya/phantomjs/issues/12160
    if (value.indexOf('\\') != -1) {
      value = value.replace("\\", "");
    }

    return TypeConvert.fromJson(value, type);
  }

  @Override
  public String value(String name, String defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : value;
  }

  @Override
  public int value(String name, int defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }

  @Override
  public long value(String name, long defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Long.parseLong(value);
  }

  @Override
  public boolean value(String name, boolean defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }
}
