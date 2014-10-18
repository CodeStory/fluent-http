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

import net.codestory.http.internal.*;

public interface Query extends Unwrappable {
  String get(String name);

  Iterable<String> all(String name);

  Map<String, String> keyValues();

  default int getInteger(String name) {
    String value = get(name);
    return (value != null) ? Integer.parseInt(value) : 0;
  }

  default long getLong(String name) {
    String value = get(name);
    return (value != null) ? Long.parseLong(value) : 0L;
  }

  default float getFloat(String name) {
    String value = get(name);
    return (value != null) ? Float.parseFloat(value) : 0.0f;
  }

  default double getDouble(String name) {
    String value = get(name);
    return (value != null) ? Double.parseDouble(value) : 0.0d;
  }

  default boolean getBoolean(String name) {
    String value = get(name);
    return (value != null) ? Boolean.valueOf(value) : false;
  }
}
