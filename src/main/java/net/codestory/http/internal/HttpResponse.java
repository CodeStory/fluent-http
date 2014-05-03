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

import org.simpleframework.http.*;

public class HttpResponse {
  private final Response response;

  public HttpResponse(Response response) {
    this.response = response;
  }

  public void close() throws IOException {
    response.close();
  }

  public OutputStream outputStream() throws IOException {
    return response.getOutputStream();
  }

  public void setContentLength(long length) {
    response.setContentLength(length);
  }

  public void setValue(String name, String value) {
    response.setValue(name, value);
  }

  public void setStatus(int statusCode) {
    response.setStatus(Status.getStatus(statusCode));
  }

  public void setCookie(NewCookie cookie) {
    response.setCookie(cookie);
  }
}
