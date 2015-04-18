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

import java.io.*;
import java.lang.reflect.*;
import java.util.function.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.*;
import com.fasterxml.jackson.datatype.jsr310.*;

public class TypeConvert {
  private static ObjectMapper CURRENT_OBJECT_MAPPER = createDefaultObjectMapper();

  private TypeConvert() {
    // static class
  }

  private static ObjectMapper createDefaultObjectMapper() {
    return new ObjectMapper()
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
      .registerModule(new JSR310Module())
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static void configureOrReplaceMapper(Function<ObjectMapper, ObjectMapper> configureOrReplace) {
    ObjectMapper defaultObjectMapper = createDefaultObjectMapper();
    ObjectMapper replacementObjectMapper = configureOrReplace.apply(defaultObjectMapper);
    CURRENT_OBJECT_MAPPER = replacementObjectMapper;
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return CURRENT_OBJECT_MAPPER.readValue(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to parse json", e);
    }
  }

  public static <T> T fromJson(String json, Type type) {
    try {
      return CURRENT_OBJECT_MAPPER.readValue(json, TypeFactory.defaultInstance().constructType(type));
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to parse json", e);
    }
  }

  public static <T> T fromJson(String json, TypeReference<T> type) {
    try {
      return CURRENT_OBJECT_MAPPER.readValue(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to parse json", e);
    }
  }

  public static <T> T convertValue(Object value, Class<T> type) {
    T converted = CURRENT_OBJECT_MAPPER.convertValue(value, type);
    if (converted == null) {
      converted = PrimitiveDefaultValues.INSTANCE.get(type);
    }
    return converted;
  }

  public static Object convertValue(Object value, Type type) {
    Object converted = CURRENT_OBJECT_MAPPER.convertValue(value, TypeFactory.defaultInstance().constructType(type));
    if ((converted == null) && (type instanceof Class<?>)) {
      converted = PrimitiveDefaultValues.INSTANCE.get((Class<?>) type);
    }
    return converted;
  }

  public static <T> T convertValue(Object value, TypeReference<T> type) {
    return CURRENT_OBJECT_MAPPER.convertValue(value, type);
  }

  public static byte[] toByteArray(Object value) {
    try {
      return CURRENT_OBJECT_MAPPER.writer().writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to serialize to json", e);
    }
  }

  public static String toJson(Object value) {
    try {
      return CURRENT_OBJECT_MAPPER.writer().writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to serialize to json", e);
    }
  }
}
