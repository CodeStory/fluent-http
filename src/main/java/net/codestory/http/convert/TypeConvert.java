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
package net.codestory.http.convert;

import java.util.*;

import net.codestory.http.internal.*;

import com.fasterxml.jackson.databind.*;

public class TypeConvert {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private TypeConvert() {
    // static class
  }

  public static Object[] convert(Context context, String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length + 1];

    converted[0] = convert(context, types[0]);
    for (int i = 0; i < values.length; i++) {
      converted[i + 1] = convert(values[i], types[i]);
    }

    return converted;
  }

  public static Object[] convert(String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length];

    for (int i = 0; i < values.length; i++) {
      converted[i] = convert(values[i], types[i]);
    }

    return converted;
  }

  public static Object convert(String value, Class<?> type) {
    return OBJECT_MAPPER.convertValue(value, type);
  }

  public static Object convert(Context context, Class<?> type) {
    if (type.isAssignableFrom(Context.class)) {
      return context;
    } else if (type.isAssignableFrom(Map.class)) {
      return context.getKeyValues();
    }
    return OBJECT_MAPPER.convertValue(context.getKeyValues(), type);
  }
}
