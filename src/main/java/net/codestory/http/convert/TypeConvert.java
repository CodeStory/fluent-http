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

import java.io.*;

import net.codestory.http.internal.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

public class TypeConvert {
  private static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private TypeConvert() {
    // static class
  }

  public static void overrideMapper(ObjectMapper mapper) {
    OBJECT_MAPPER = mapper;
  }

  public static Object[] convert(Context context, String[] pathParameters, Class<?>... types) {
    Object[] converted = new Object[types.length];

    // String parameters
    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = convertValue(pathParameters[i], types[i]);
    }

    // Other parameters
    for (int i = pathParameters.length; i < converted.length; i++) {
      converted[i] = context.extract(types[i]);
    }

    return converted;
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return OBJECT_MAPPER.readValue(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to parse json", e);
    }
  }

  public static <T> T convertValue(Object value, Class<T> type) {
    return OBJECT_MAPPER.convertValue(value, type);
  }

  public static byte[] toByteArray(Object value) {
    try {
      return OBJECT_MAPPER.writer().writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to serialize to json", e);
    }
  }

  public static String toJson(Object value) {
    try {
      return OBJECT_MAPPER.writer().writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to serialize to json", e);
    }
  }
}
