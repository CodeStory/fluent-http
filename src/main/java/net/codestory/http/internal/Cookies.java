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

import net.codestory.http.convert.*;

import org.simpleframework.http.*;

public class Cookies implements Iterable<Cookie> {
  private final Request request;

  public Cookies(Request request) {
    this.request = request;
  }

  @Override
  // TODO: Hide Simple implementation
  public Iterator<Cookie> iterator() {
    return list().iterator();
  }

  // TODO: Hide Simple implementation
  public List<Cookie> list() {
    return request.getCookies();
  }

  // TODO: Hide Simple implementation
  public Cookie get(String name) {
    return request.getCookie(name);
  }

  public String value(String name) {
    Cookie cookie = get(name);
    return (cookie == null) ? null : cookie.getValue();
  }

  public Map<String, String> keyValues() {
    Map<String, String> keyValues = new HashMap<>();
    for (Cookie cookie : request.getCookies()) {
      keyValues.put(cookie.getName(), cookie.getValue());
    }
    return keyValues;
  }

  @SuppressWarnings("unchecked")
  public <T> T value(String name, T defaultValue) {
    T value = value(name, (Class<T>) defaultValue.getClass());
    return (value == null) ? defaultValue : value;
  }

  @SuppressWarnings("unchecked")
  public <T> T value(String name, Class<T> type) {
    String value = value(name);
    return (value == null) ? null : TypeConvert.fromJson(value, type);
  }

  public String value(String name, String defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : value;
  }

  public int value(String name, int defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }

  public long value(String name, long defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Long.parseLong(value);
  }

  public boolean value(String name, boolean defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }
}
