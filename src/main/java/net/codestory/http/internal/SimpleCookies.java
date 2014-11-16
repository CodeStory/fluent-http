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

import net.codestory.http.*;

import org.simpleframework.http.Request;

class SimpleCookies implements Cookies {
  private final Request request;

  SimpleCookies(Request request) {
    this.request = request;
  }

  @Override
  public Iterator<Cookie> iterator() {
    return request.getCookies().stream().map(cookie -> (Cookie) new SimpleCookie(cookie)).iterator();
  }

  @Override
  public Cookie get(String name) {
    org.simpleframework.http.Cookie cookie = request.getCookie(name);
    return (cookie == null) ? null : new SimpleCookie(cookie);
  }

  // Implementation more performant than the default one
  // because here we don't wrap the cookie.
  //
  @Override
  public String value(String name) {
    org.simpleframework.http.Cookie cookie = request.getCookie(name);
    return (cookie == null) ? null : cookie.getValue();
  }

  // Implementation more performant than the default one
  // because here we don't wrap every native cookie.
  //
  @Override
  public Map<String, String> keyValues() {
    Map<String, String> keyValues = new HashMap<>();
    request.getCookies().forEach(cookie -> keyValues.put(cookie.getName(), cookie.getValue()));
    return keyValues;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(request) ? (T) request : null;
  }
}
