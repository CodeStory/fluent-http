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
package net.codestory.http.constants;

import java.util.Arrays;
import java.util.List;

public abstract class Methods {
  private Methods() {
    // Do not allow subclassing
  }

  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String HEAD = "HEAD";
  public static final String OPTIONS = "OPTIONS";
  public static final String TRACE = "TRACE";
  public static final String CONNECT = "CONNECT";

  public static final List<String> HTTP_METHODS = Arrays.asList(
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT
  );

  public static final List<String> COMPLEX_HTTP_METHODS = Arrays.asList(
    PUT,
    DELETE,
    TRACE,
    CONNECT
  );
  public static final List<String> SIMPLE_HTTP_METHODS = Arrays.asList(
    GET,
    POST,
    HEAD
  );
}