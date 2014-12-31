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
package net.codestory.http;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.cors.CORSRequestType;
import net.codestory.http.internal.*;
import net.codestory.http.io.InputStreams;

import static java.util.stream.Collectors.toMap;
import static net.codestory.http.constants.Headers.ACCESS_CONTROL_REQUEST_METHOD;
import static net.codestory.http.constants.Headers.ORIGIN;
import static net.codestory.http.constants.Headers.X_FORWARDED_FOR;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.constants.Methods.CONNECT;
import static net.codestory.http.cors.CORSRequestType.*;
import static net.codestory.http.cors.CORSRequestType.PRE_FLIGHT;
import static net.codestory.http.types.ContentTypes.SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES;

public interface Request extends Unwrappable {
  String uri();

  String method();

  String content() throws IOException;

  default byte[] contentAsBytes() throws IOException {
    return InputStreams.readBytes(inputStream());
  }

  default <T> T contentAs(Class<T> type) throws IOException {
    if (isUrlEncodedForm()) {
      return TypeConvert.convertValue(query().keyValues(), type);
    }
    return TypeConvert.fromJson(content(), type);
  }

  @SuppressWarnings("unchecked")
  default <T> T contentAs(Type type) throws IOException {
    if (isUrlEncodedForm()) {
      return (T) TypeConvert.convertValue(query().keyValues(), type);
    }
    return TypeConvert.fromJson(content(), type);
  }

  default <T> T contentAs(TypeReference<T> type) throws IOException {
    if (isUrlEncodedForm()) {
      return TypeConvert.convertValue(query().keyValues(), type);
    }
    return TypeConvert.fromJson(content(), type);
  }

  String contentType();

  List<String> headerNames();

  List<String> headers(String name);

  String header(String name);

  default String header(String name, String defaultValue) {
    return Optional.ofNullable(header(name)).orElse(defaultValue);
  }

  default Map<String, List<String>> headers() {
    return headerNames().stream().collect(toMap(name -> name, name -> headers(name)));
  }

  InputStream inputStream() throws IOException;

  InetSocketAddress clientAddress();

  default String clientAddressForwarded() {
    String forwarded = header(X_FORWARDED_FOR);
    return (forwarded != null) ? forwarded : clientAddress().toString();
  }

  default boolean isUrlEncodedForm() {
    String contentType = header("Content-Type");
    return (contentType != null) && (contentType.contains("application/x-www-form-urlencoded"));
  }

  boolean isSecure();

  Cookies cookies();

  Query query();

  List<Part> parts();



  default CORSRequestType corsRequestType() {
    String origin = header(ORIGIN);
    if (origin == null) {
      return NOT_CORS;
    }

    if (isInvalidOrigin(origin)) {
      return INVALID_CORS;
    }

    switch (method()) {
      case OPTIONS:
        String accessControl = header(ACCESS_CONTROL_REQUEST_METHOD);
        if (accessControl == null) {
          return ACTUAL;
        }
        return accessControl.isEmpty() ? INVALID_CORS : PRE_FLIGHT;
      case GET:
      case HEAD:
        return SIMPLE;
      case POST:
        String contentType = contentType();
        if (contentType == null) {
          return INVALID_CORS;
        }
        return SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(contentType.toLowerCase().trim()) ? SIMPLE : ACTUAL;
      case PUT:
      case DELETE:
      case TRACE:
      case CONNECT:
        return ACTUAL;
      default:
        return INVALID_CORS;
    }
  }

  default boolean isCORS() {
    return corsRequestType() != NOT_CORS;
  }

  default boolean isPreflight() {
    return corsRequestType() == PRE_FLIGHT;
  }

  /*private static*/ default boolean isInvalidOrigin(String origin) {
    if (origin.isEmpty() || origin.contains("%")) {
      return true;
    }
    try {
      return new URI(origin).getScheme() == null;
    } catch (URISyntaxException e) {
      return true;
    }
  }
}
