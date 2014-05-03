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
import java.net.*;
import java.util.*;

import net.codestory.http.exchange.*;

import org.simpleframework.http.*;
import org.simpleframework.transport.*;

class SimpleHttpRequest implements HttpRequest {
  private final Request request;

  SimpleHttpRequest(Request request) {
    this.request = request;
  }

  @Override
  public String uri() {
    return request.getPath().getPath();
  }

  @Override
  public String method() {
    return request.getMethod();
  }

  @Override
  public String header(String name) {
    return request.getValue(name);
  }

  @Override
  public String content() throws IOException {
    return request.getContent();
  }

  @Override
  public InputStream inputStream() throws IOException {
    return request.getInputStream();
  }

  @Override
  public List<String> headers(String name) {
    return request.getValues(name);
  }

  @Override
  public InetSocketAddress clientAddress() {
    return request.getClientAddress();
  }

  @Override
  public boolean isSecure() {
    return request.isSecure();
  }

  @Override
  public Cookies cookies() {
    return new SimpleCookies(request);
  }

  @Override
  public HttpQuery query() {
    return new SimpleHttpQuery(request.getQuery());
  }

  @Override
  public Certificate clientCertificate() {
    return request.getClientCertificate();
  }
}
