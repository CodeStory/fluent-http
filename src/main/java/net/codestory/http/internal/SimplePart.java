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

import java.io.*;

import net.codestory.http.*;

class SimplePart implements Part {
  private final org.simpleframework.http.Part part;

  SimplePart(org.simpleframework.http.Part part) {
    this.part = part;
  }

  @Override
  public boolean isFile() {
    return part.isFile();
  }

  @Override
  public String name() {
    return part.getName();
  }

  @Override
  public String fileName() {
    return part.getFileName();
  }

  @Override
  public String header(String name) {
    return part.getHeader(name);
  }

  @Override
  public String content() throws IOException {
    return part.getContent();
  }

  @Override
  public InputStream inputStream() throws IOException {
    return part.getInputStream();
  }

  @Override
  public String contentType() {
    return part.getContentType().toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(part) ? (T) part : null;
  }
}
