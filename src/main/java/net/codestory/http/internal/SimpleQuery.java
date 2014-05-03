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
package net.codestory.http.internal;

import java.util.*;

import net.codestory.http.exchange.*;

import org.simpleframework.http.*;

class SimpleQuery implements HttpQuery {
  private final Query query;

  SimpleQuery(Query query) {
    this.query = query;
  }

  @Override
  public String get(String name) {
    return query.get(name);
  }

  @Override
  public Iterable<String> all(String name) {
    return query.getAll(name);
  }

  @Override
  public int getInteger(String name) {
    String value = get(name);
    return (value != null) ? Integer.parseInt(value) : 0;
  }

  @Override
  public float getFloat(String name) {
    String value = get(name);
    return (value != null) ? Float.parseFloat(value) : 0.0f;
  }

  @Override
  public boolean getBoolean(String name) {
    String value = get(name);
    return (value != null) ? Boolean.valueOf(value) : false;
  }

  @Override
  public Map<String, String> keyValues() {
    return query;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(query) ? (T) query : null;
  }
}
