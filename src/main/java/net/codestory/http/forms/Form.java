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
package net.codestory.http.forms;

import java.util.*;
import java.util.function.*;

public class Form {
  private final Map<String, String> keyValues;

  public Form(Map<String, String> keyValues) {
    this.keyValues = keyValues;
  }

  public void forEach(BiConsumer<? super String, ? super String> action) {
    keyValues.forEach(action);
  }

  public Set<String> keys() {
    return keyValues.keySet();
  }

  public String get(String name) {
    return keyValues.get(name);
  }

  public String get(String name, String defaultValue) {
    String value = get(name);
    return (value != null) ? value : defaultValue;
  }

  public int getInteger(String name) {
    return getInteger(name, 0);
  }

  public int getInteger(String name, int defaultValue) {
    String value = get(name);
    return (value != null) ? Integer.parseInt(value) : defaultValue;
  }

  public long getLong(String name) {
    return getLong(name, 0L);
  }

  public long getLong(String name, long defaultValue) {
    String value = get(name);
    return (value != null) ? Long.parseLong(value) : defaultValue;
  }

  public float getFloat(String name) {
    return getFloat(name, 0f);
  }

  public float getFloat(String name, float defaultValue) {
    String value = get(name);
    return (value != null) ? Float.parseFloat(value) : defaultValue;
  }

  public double getDouble(String name) {
    return getDouble(name, 0.0);
  }

  public double getDouble(String name, double defaultValue) {
    String value = get(name);
    return (value != null) ? Double.parseDouble(value) : defaultValue;
  }

  public boolean getBoolean(String name) {
    return getBoolean(name, false);
  }

  public boolean getBoolean(String name, boolean defaultValue) {
    String value = get(name);
    return (value != null) ? Boolean.valueOf(value) : defaultValue;
  }
}
