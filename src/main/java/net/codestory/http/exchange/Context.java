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
package net.codestory.http.exchange;

import static net.codestory.http.constants.Headers.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.codestory.http.convert.*;
import net.codestory.http.injection.*;
import net.codestory.http.io.*;
import net.codestory.http.security.*;

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
