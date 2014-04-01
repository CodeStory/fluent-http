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

import static net.codestory.http.constants.Headers.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import net.codestory.http.constants.Headers;
import net.codestory.http.constants.Methods;
import net.codestory.http.convert.*;
import net.codestory.http.injection.*;
import net.codestory.http.io.*;
import net.codestory.http.security.*;

import net.codestory.http.types.ContentTypes;
import org.simpleframework.http.*;

public class Context {
  private final Request request;
  private final Response response;
  private final IocAdapter iocAdapter;
  private final Query query;
  private User currentUser;

  public Context(Request request, Response response, IocAdapter iocAdapter) {
    this.request = request;
    this.response = response;
    this.iocAdapter = iocAdapter;
    this.query = request.getQuery();
  }

  public static enum CORSRequestType {
    SIMPLE,
    ACTUAL,
    PRE_FLIGHT,
    NOT_CORS,
    INVALID_CORS
  }

  public CORSRequestType corsRequestType() {
    CORSRequestType requestType = CORSRequestType.INVALID_CORS;
    String originHeader = getHeader(Headers.ORIGIN);
    if (originHeader != null) {
      if (originHeader.isEmpty()) {
        requestType = CORSRequestType.INVALID_CORS;
      } else if (!isValidOrigin(originHeader)) {
        requestType = CORSRequestType.INVALID_CORS;
      } else {
        String method = method();
        if (method != null && Methods.HTTP_METHODS.contains(method)) {
          if (Method.OPTIONS.equals(method)) {
            String accessControlRequestMethodHeader =
                    getHeader(Headers.ACCESS_CONTROL_REQUEST_METHOD);
            if (accessControlRequestMethodHeader != null
                    && !accessControlRequestMethodHeader.isEmpty()) {
              requestType = CORSRequestType.PRE_FLIGHT;
            } else if (accessControlRequestMethodHeader != null
                    && accessControlRequestMethodHeader.isEmpty()) {
              requestType = CORSRequestType.INVALID_CORS;
            } else {
              requestType = CORSRequestType.ACTUAL;
            }
          } else if (Method.GET.equals(method) || Method.HEAD.equals(method)) {
            requestType = CORSRequestType.SIMPLE;
          } else if (Method.POST.equals(method)) {
            String contentType = request.getContentType().getType();
            if (contentType != null) {
              contentType = contentType.toLowerCase().trim();
              if (ContentTypes.SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES
                      .contains(contentType)) {
                requestType = CORSRequestType.SIMPLE;
              } else {
                requestType = CORSRequestType.ACTUAL;
              }
            }
          } else if (Methods.COMPLEX_HTTP_METHODS.contains(method)) {
            requestType = CORSRequestType.ACTUAL;
          }
        }
      }
    } else {
      requestType = CORSRequestType.NOT_CORS;
    }
    return requestType;
  }


  public boolean isCORS() {
    return !corsRequestType().equals(CORSRequestType.NOT_CORS);
  }

  public boolean isPreflight() {
    return corsRequestType().equals(CORSRequestType.PRE_FLIGHT);
  }

  private boolean isValidOrigin(String origin) {
    if (origin.contains("%")) {
      return false;
    }
    URI originURI;
    try {
      originURI = new URI(origin);
    } catch (URISyntaxException e) {
      return false;
    }
    return originURI.getScheme() != null;
  }

  public String uri() {
    return request.getPath().getPath();
  }

  public Cookie cookie(String name) {
    return request.getCookie(name);
  }

  public String cookieValue(String name) {
    Cookie cookie = cookie(name);
    return (cookie == null) ? null : cookie.getValue();
  }

  @SuppressWarnings("unchecked")
  public <T> T cookieValue(String name, T defaultValue) {
    T value = cookieValue(name, (Class<T>) defaultValue.getClass());
    return (value == null) ? defaultValue : value;
  }

  @SuppressWarnings("unchecked")
  public <T> T cookieValue(String name, Class<T> type) {
    String value = cookieValue(name);
    return (value == null) ? null : TypeConvert.fromJson(value, type);
  }

  public String cookieValue(String name, String defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : value;
  }

  public int cookieValue(String name, int defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }

  public long cookieValue(String name, long defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Long.parseLong(value);
  }

  public boolean cookieValue(String name, boolean defaultValue) {
    String value = cookieValue(name);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }

  public List<Cookie> cookies() {
    return request.getCookies();
  }

  public String get(String name) {
    return query.get(name);
  }

  public List<String> getAll(String name) {
    return query.getAll(name);
  }

  public int getInteger(String name) {
    return query.getInteger(name);
  }

  public float getFloat(String name) {
    return query.getFloat(name);
  }

  public boolean getBoolean(String name) {
    return query.getBoolean(name);
  }

  public String getHeader(String name) {
    return request.getValue(name);
  }

  public List<String> getHeaders(String name) {
    return request.getValues(name);
  }

  public String method() {
    return request.getMethod();
  }

  public Map<String, String> keyValues() {
    return query;
  }

  public String getClientAddress() {
    String forwarded = getHeader(X_FORWARDED_FOR);
    return (forwarded != null) ? forwarded : request.getClientAddress().toString();
  }

  public Request request() {
    return request;
  }

  public Response response() {
    return response;
  }

  public byte[] content() {
    try {
      return InputStreams.readBytes(request.getInputStream());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public String contentAsString() {
    try {
      return request.getContent();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public <T> T contentAs(Class<T> type) {
    return TypeConvert.convert(this, type);
  }

  public <T> T getBean(Class<T> type) {
    return iocAdapter.get(type);
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public User currentUser() {
    return currentUser;
  }
}
