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
package net.codestory.http;

import net.codestory.http.internal.Unwrappable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface Response extends Unwrappable {
  void close() throws IOException;

  OutputStream outputStream() throws IOException;

  void setContentLength(long length);

  void setHeader(String name, String value);

  void setStatus(int statusCode);

  void setCookie(Cookie cookie);

  default void setCookies(Iterable<Cookie> cookies) {
    cookies.forEach(this::setCookie);
  }

  default void setHeaders(Map<String, String> headers) {
    headers.forEach(this::setHeader);
  }
}
