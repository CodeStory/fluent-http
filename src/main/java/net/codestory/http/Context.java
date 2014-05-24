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

import net.codestory.http.constants.Headers;
import net.codestory.http.constants.Methods;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.injection.IocAdapter;
import net.codestory.http.io.InputStreams;
import net.codestory.http.security.User;
import net.codestory.http.types.ContentTypes;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static net.codestory.http.constants.Headers.X_FORWARDED_FOR;

public class Context {
  private final Request request;
  private final Response response;
  private final IocAdapter iocAdapter;
  private User currentUser;

  public Context(Request request, Response response, IocAdapter iocAdapter) {
    this.request = request;
    this.response = response;
    this.iocAdapter = iocAdapter;
  }

  public Request request() {
    return request;
  }

  public Response response() {
    return response;
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
        String originHeader = header(Headers.ORIGIN);
        if (originHeader != null) {
            if (originHeader.isEmpty()) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (!isValidOrigin(originHeader)) {
                requestType = CORSRequestType.INVALID_CORS;
            } else {
                String method = method();
                if (method != null && Methods.HTTP_METHODS.contains(method)) {
                    if (Methods.OPTIONS.equals(method)) {
                        String accessControlRequestMethodHeader =
                                header(Headers.ACCESS_CONTROL_REQUEST_METHOD);
                        if (accessControlRequestMethodHeader != null
                                && !accessControlRequestMethodHeader.isEmpty()) {
                            requestType = CORSRequestType.PRE_FLIGHT;
                        } else if (accessControlRequestMethodHeader != null) {
                            requestType = CORSRequestType.INVALID_CORS;
                        } else {
                            requestType = CORSRequestType.ACTUAL;
                        }
                    } else if (Methods.GET.equals(method) || Methods.HEAD.equals(method)) {
                        requestType = CORSRequestType.SIMPLE;
                    } else if (Methods.POST.equals(method)) {
                        String contentType = request.contentType();
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
    return request.uri();
  }

  public Cookies cookies() {
    return request.cookies();
  }

  public List<Part> parts() {
    return request.parts();
  }

  public Query query() {
    return request.query();
  }

  public String get(String key) {
    return request.query().get(key);
  }

  public String header(String name) {
    return request.header(name);
  }

  public List<String> headers(String name) {
    return request.headers(name);
  }

  public String method() {
    return request.method();
  }

  public String clientAddress() {
    String forwarded = header(X_FORWARDED_FOR);
    return (forwarded != null) ? forwarded : request.clientAddress().toString();
  }

  public byte[] content() {
    try {
      return InputStreams.readBytes(request.inputStream());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public String contentAsString() {
    try {
      return request.content();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read content", e);
    }
  }

  public <T> T contentAs(Class<T> type) {
    try {
      String json = request.content();

      return TypeConvert.fromJson(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable read content", e);
    }
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

  public boolean isUrlEncodedForm() {
    String contentType = header("Content-Type");
    return (contentType != null) && (contentType.contains("application/x-www-form-urlencoded"));
  }

  public Object extract(Type type) {
    if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;

      if (clazz.isAssignableFrom(Context.class)) {
        return this;
      }
      if (clazz.isAssignableFrom(Request.class)) {
        return request();
      }
      if (clazz.isAssignableFrom(Response.class)) {
        return response();
      }
      if (clazz.isAssignableFrom(Cookies.class)) {
        return cookies();
      }
      if (clazz.isAssignableFrom(Query.class)) {
        return query();
      }
      if (clazz.isAssignableFrom(User.class)) {
        return currentUser();
      }
      if (clazz.isAssignableFrom(byte[].class)) {
        return content();
      }
      if (clazz.isAssignableFrom(String.class)) {
        return contentAsString();
      }
    }

    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;

      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class) {
        if (List.class.isAssignableFrom((Class<?>) rawType)) {
          Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
          if (actualTypeArguments.length == 1) {
            Type argument = actualTypeArguments[0];
            if (argument instanceof Class) {
              if (Part.class.isAssignableFrom((Class<?>) argument)) {
                return parts();
              }
            }
          }
        } else if (Map.class.isAssignableFrom((Class<?>) rawType)) {
          return query().keyValues();
        }
      }
    }

    if (isUrlEncodedForm()) {
      return TypeConvert.convertValue(query().keyValues(), type);
    }

    try {
      String json = request.content();

      return TypeConvert.fromJson(json, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable read content", e);
    }
  }
}
