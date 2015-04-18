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
package net.codestory.http.convert;

import java.util.*;

public enum PrimitiveDefaultValues {
  INSTANCE;

  private final Map<Class<?>, Object> defaults;

  PrimitiveDefaultValues() {
    Map<Class<?>, Object> map = new HashMap<>();
    put(map, boolean.class, false);
    put(map, char.class, '\0');
    put(map, byte.class, (byte) 0);
    put(map, short.class, (short) 0);
    put(map, int.class, 0);
    put(map, long.class, 0L);
    put(map, float.class, 0f);
    put(map, double.class, 0d);
    defaults = Collections.unmodifiableMap(map);
  }

  private <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
    map.put(type, value);
  }

  public <T> T get(Class<T> type) {
    return (T) defaults.get(type);
  }
}
