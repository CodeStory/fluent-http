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

import java.util.*;

import net.codestory.http.convert.*;

public interface Cookies extends Iterable<Cookie> {
  Cookie get(String name);

  public default Map<String, String> keyValues() {
    Map<String, String> keyValues = new HashMap<>();
    forEach(cookie -> keyValues.put(cookie.name(), cookie.value()));
    return keyValues;
  }

  public default String value(String name) {
    Cookie cookie = get(name);
    return (cookie == null) ? null : cookie.value();
  }

  public default String value(String name, String defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : value;
  }

  public default int value(String name, int defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }

  public default long value(String name, long defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Long.parseLong(value);
  }

  public default boolean value(String name, boolean defaultValue) {
    String value = value(name);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }

  @SuppressWarnings("unchecked")
  public default <T> T value(String name, T defaultValue) {
    T value = value(name, (Class<T>) defaultValue.getClass());
    return (value == null) ? defaultValue : value;
  }

  @SuppressWarnings("unchecked")
  public default <T> T value(String name, Class<T> type) {
    String value = value(name);
    if (value == null) {
      return null;
    }

    // fix for https://github.com/ariya/phantomjs/issues/12160
    if (value.indexOf('\\') != -1) {
      value = value.replace("\\", "");
    }

    return TypeConvert.fromJson(value, type);
  }
}
