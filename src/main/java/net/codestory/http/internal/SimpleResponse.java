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

import java.io.*;

import net.codestory.http.exchange.Cookie;
import net.codestory.http.exchange.Response;

import org.simpleframework.http.*;

class SimpleResponse implements Response {
  private final org.simpleframework.http.Response response;

  SimpleResponse(org.simpleframework.http.Response response) {
    this.response = response;
  }

  @Override
  public void close() throws IOException {
    response.close();
  }

  @Override
  public OutputStream outputStream() throws IOException {
    return response.getOutputStream();
  }

  @Override
  public void setContentLength(long length) {
    response.setContentLength(length);
  }

  @Override
  public void setValue(String name, String value) {
    response.setValue(name, value);
  }

  @Override
  public void setStatus(int statusCode) {
    response.setStatus(Status.getStatus(statusCode));
  }

  @Override
  public void setCookie(Cookie newCookie) {
    org.simpleframework.http.Cookie cookie = new org.simpleframework.http.Cookie(newCookie.name(), newCookie.value(), newCookie.path(), newCookie.isNew());
    cookie.setExpiry(newCookie.expiry());
    cookie.setVersion(newCookie.version());
    cookie.setSecure(newCookie.isSecure());
    cookie.setProtected(newCookie.isHttpOnly());
    cookie.setDomain(newCookie.domain());

    response.setCookie(cookie);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(response) ? (T) response : null;
  }
}
