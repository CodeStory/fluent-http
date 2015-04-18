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
package net.codestory.http.websockets;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum WebSocketJsonParser {
  INSTANCE;

  private final ObjectMapper objectMapper = new ObjectMapper()
    .setVisibility(FIELD, ANY)
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

  public byte[] toJson(Object object) {
    try {
      return objectMapper.writeValueAsBytes(object);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to write as json", e);
    }
  }

  public <T> T as(String text, Class<T> type) {
    try {
      return objectMapper.readValue(text, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to parse json", e);
    }
  }
}
