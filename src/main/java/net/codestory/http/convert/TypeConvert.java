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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

public class TypeConvert {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

  private TypeConvert() {
    // static class
  }

  public static Object[] convert(String[] pathParameters, Context context, Class<?>[] types) {
    Object[] converted = new Object[pathParameters.length + 1];

    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = convert(pathParameters[i], types[i]);
    }
    converted[converted.length - 1] = convert(context, types[converted.length - 1]);

    return converted;
  }

  public static Object[] convert(String[] pathParameters, Class<?>[] types) {
    Object[] converted = new Object[pathParameters.length];

    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = convert(pathParameters[i], types[i]);
    }

    return converted;
  }

  public static Object convert(Context context, Class<?> type) {
    if (type.isAssignableFrom(Context.class)) {
      return context;
    }
    if (type.isAssignableFrom(Map.class)) {
      return context.keyValues();
    }
    return convert(context.keyValues(), type);
  }

  public static <T> T convert(String value, Class<T> type) {
    return OBJECT_MAPPER.convertValue(value, type);
  }

  public static <T> T convert(Map<String, String> keyValues, Class<T> type) {
    return OBJECT_MAPPER.convertValue(keyValues, type);
  }

  public static byte[] toByteArray(Object value) throws JsonProcessingException {
    return OBJECT_MAPPER.writer().writeValueAsBytes(value);
  }
}
