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
package net.codestory.http.templating;

import java.util.*;

public class Model {
  private final Map<String, Object> keyValues = new HashMap<>();

  private Model() {
    // Static constructor
  }

  public Map<String, Object> keyValues() {
    return keyValues;
  }

  public Object get(String key) {
    return keyValues.get(key);
  }

  public static Model of() {
    return new Model();
  }

  public static Model of(String key, Object value) {
    return of().add(key, value);
  }

  public static Model of(String k1, Object v1, String k2, Object v2) {
    return of().add(k1, v1).add(k2, v2);
  }

  public static Model of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return of().add(k1, v1).add(k2, v2).add(k3, v3);
  }

  public static Model of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return of().add(k1, v1).add(k2, v2).add(k3, v3).add(k4, v4);
  }

  private Model add(String key, Object value) {
    keyValues.put(key, value);
    return this;
  }
}
