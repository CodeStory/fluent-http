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

import org.simpleframework.http.*;

import com.fasterxml.jackson.databind.*;

public class TypeConvert {
  private TypeConvert() {
    // static class
  }

  public static Object[] convert(Query query, String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length + 1];
    converted[0] = TypeConvert.convert(query, types[0]);
    for (int i = 0; i < values.length; i++) {
      converted[i + 1] = TypeConvert.convert(values[i], types[i]);
    }
    return converted;
  }

  public static Object[] convert(String[] values, Class<?>[] types) {
    Object[] converted = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      converted[i] = TypeConvert.convert(values[i], types[i]);
    }
    return converted;
  }

  public static Object convert(String value, Class<?> type) {
    if ((type == int.class) || (type == Integer.class)) {
      return Integer.parseInt(value);
    } else if ((type == float.class) || (type == Float.class)) {
      return Float.parseFloat(value);
    } else if ((type == long.class) || (type == Long.class)) {
      return Long.parseLong(value);
    } else if ((type == double.class) || (type == Double.class)) {
      return Double.parseDouble(value);
    } else if ((type == boolean.class) || (type == Boolean.class)) {
      return Boolean.parseBoolean(value);
    }
    return value;
  }

  public static Object convert(Query query, Class<?> type) {
    if (type.isAssignableFrom(Map.class)) {
      return query;
    } else if (type.isAssignableFrom(Query.class)) {
      return query;
    }
    return new ObjectMapper().convertValue(query, type);
  }
}
