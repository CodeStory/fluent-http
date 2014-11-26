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
package net.codestory.http;

import java.io.*;
import java.net.*;
import java.util.*;

import net.codestory.http.internal.*;

import static java.util.stream.Collectors.toMap;

public interface Request extends Unwrappable {
  String uri();

  String method();

  String content() throws IOException;

  String contentType();

  List<String> headerNames();

  List<String> headers(String name);

  public default String header(String name) {
    return header(name, null);
  }

  public default String header(String name, String defaultValue) {
    return headers(name).stream().findFirst().orElse(defaultValue);
  }

  public default Map<String, List<String>> headers() {
    return headerNames().stream().collect(toMap(name -> name, name -> headers(name)));
  }

  InputStream inputStream() throws IOException;

  InetSocketAddress clientAddress();

  boolean isSecure();

  Cookies cookies();

  Query query();

  List<Part> parts();
}
