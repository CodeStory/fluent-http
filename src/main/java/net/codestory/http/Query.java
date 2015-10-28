/**
 * Copyright (C) 2013-2015 all@code-story.net
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
import java.util.stream.Collectors;

import net.codestory.http.internal.*;

public interface Query extends Unwrappable {
  Collection<String> keys();

  Iterable<String> all(String name);

  default String get(String name) {
    Iterator<String> values = all(name).iterator();
    return values.hasNext() ? values.next() : null;
  }

  default Map<String, String> keyValues() {
    return keys().stream().collect(Collectors.toMap(key -> key, this::get));
  }

  default int getInteger(String name) {
    return getInteger(name, 0);
  }

  default int getInteger(String name, int defaultValue) {
    String value = get(name);
    return (value != null) ? Integer.parseInt(value) : defaultValue;
  }

  default long getLong(String name) {
    return getLong(name, 0L);
  }

  default long getLong(String name, long defaultValue) {
    String value = get(name);
    return (value != null) ? Long.parseLong(value) : defaultValue;
  }

  default float getFloat(String name) {
    return getFloat(name, 0.0f);
  }

  default float getFloat(String name, float defaultValue) {
    String value = get(name);
    return (value != null) ? Float.parseFloat(value) : defaultValue;
  }

  default double getDouble(String name) {
    return getDouble(name, 0.0d);
  }

  default double getDouble(String name, double defaultValue) {
    String value = get(name);
    return (value != null) ? Double.parseDouble(value) : defaultValue;
  }

  default boolean getBoolean(String name) {
    return getBoolean(name, false);
  }

  default boolean getBoolean(String name, boolean defaultValue) {
    String value = get(name);
    return (value != null) ? Boolean.valueOf(value) : defaultValue;
  }
}
