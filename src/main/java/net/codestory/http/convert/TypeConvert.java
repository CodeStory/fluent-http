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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.codestory.http.internal.Context;

import java.io.IOException;
import java.util.Map;

public class TypeConvert {
  private static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
      .registerModule(new JSR310Module())
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private TypeConvert() {
    // static class
  }

  public static void overrideMapper(ObjectMapper mapper) {
    OBJECT_MAPPER = mapper;
  }

  public static Object[] convert(Context context, String[] pathParameters, Class<?>[] types) {
    Object[] converted = new Object[pathParameters.length + 1];

    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = convertValue(pathParameters[i], types[i]);
    }
    converted[converted.length - 1] = convert(context, types[converted.length - 1]);

    return converted;
  }

  public static Object[] convert(String[] pathParameters, Class<?>[] types) {
    Object[] converted = new Object[pathParameters.length];

    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = convertValue(pathParameters[i], types[i]);
    }

    return converted;
  }

  @SuppressWarnings("unchecked")
  public static <T> T convert(Context context, Class<T> type) {
    if (type.isAssignableFrom(Context.class)) {
      return (T) context;
    }
    if (type.isAssignableFrom(Map.class)) {
      return (T) context.keyValues();
    }
    if (isUrlEncodedForm(context)) {
      return convertValue(context.keyValues(), type);
    }

    String json;
    try {
      json = context.request().getContent();
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable read request content", e);
    }

    return fromJson(json, type);
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

  private static boolean isUrlEncodedForm(Context context) {
    String contentType = context.getHeader("Content-Type");
    return (contentType != null) && (contentType.contains("application/x-www-form-urlencoded"));
  }
}
