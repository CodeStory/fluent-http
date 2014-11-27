/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import net.codestory.http.*;

class SimpleCookie implements Cookie {
  private final org.simpleframework.http.Cookie cookie;

  SimpleCookie(org.simpleframework.http.Cookie cookie) {
    this.cookie = cookie;
  }

  @Override
  public boolean isNew() {
    return cookie.isNew();
  }

  @Override
  public String value() {
    return cookie.getValue();
  }

  @Override
  public String name() {
    return cookie.getName();
  }

  @Override
  public int version() {
    return cookie.getVersion();
  }

  @Override
  public boolean isSecure() {
    return cookie.isSecure();
  }

  @Override
  public boolean isHttpOnly() {
    return cookie.isProtected();
  }

  @Override
  public int expiry() {
    return cookie.getExpiry();
  }

  @Override
  public String path() {
    return cookie.getPath();
  }

  @Override
  public String domain() {
    return cookie.getDomain();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(cookie) ? (T) cookie : null;
  }
}
