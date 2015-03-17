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
import java.lang.reflect.*;
import java.util.*;

import net.codestory.http.injection.*;
import net.codestory.http.misc.Env;
import net.codestory.http.security.*;
import net.codestory.http.templating.*;

public class Context {
  private final Request request;
  private final Response response;
  private final IocAdapter iocAdapter;
  private final Env env;
  private final Site site;
  private User currentUser;

  public Context(Request request, Response response, IocAdapter iocAdapter, Env env, Site site) {
    this.request = request;
    this.response = response;
    this.iocAdapter = iocAdapter;
    this.env = env;
    this.site = site;
  }

  public Request request() {
    return request;
  }

  public Response response() {
    return response;
  }

  public Env env() {
    return env;
  }

  public Site site() {
    return site;
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

  public <T> T getBean(Class<T> type) {
    return iocAdapter.get(type);
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public User currentUser() {
    return currentUser;
  }

  public <T> T extract(Class<T> type) throws IOException {
    return (T) extract((Type) type);
  }

  public Object extract(Type type) throws IOException {
    if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;

      if (clazz.isAssignableFrom(Context.class)) {
        return this;
      }
      if (clazz.isAssignableFrom(Request.class)) {
        return request;
      }
      if (clazz.isAssignableFrom(Response.class)) {
        return response;
      }
      if (clazz.isAssignableFrom(Cookies.class)) {
        return cookies();
      }
      if (clazz.isAssignableFrom(Query.class)) {
        return query();
      }
      if (clazz.isAssignableFrom(User.class)) {
        return currentUser;
      }
      if (clazz.isAssignableFrom(byte[].class)) {
        return request.contentAsBytes();
      }
      if (clazz.isAssignableFrom(String.class)) {
        return request.content();
      }
      if (clazz.isAssignableFrom(InputStream.class)) {
        return request.inputStream();
      }
      if (clazz.isAssignableFrom(Site.class)) {
        return site;
      }
    }

    if (type instanceof ParameterizedType) {
      if (isListOfParts((ParameterizedType) type)) {
        return parts();
      }
      if (isGenericMap((ParameterizedType) type)) {
        return query().keyValues();
      }
    }

    return request().contentAs(type);
  }

  private static boolean isListOfParts(ParameterizedType type) {
    Type rawType = type.getRawType();
    if ((!(rawType instanceof Class)) || !List.class.isAssignableFrom((Class<?>) rawType)) {
      return false;
    }

    Type[] actualTypeArguments = type.getActualTypeArguments();
    if (actualTypeArguments.length != 1) {
      return false;
    }

    Type argument = actualTypeArguments[0];
    return (argument instanceof Class) && Part.class.isAssignableFrom((Class<?>) argument);

  }

  private static boolean isGenericMap(ParameterizedType type) {
    Type rawType = type.getRawType();
    return (rawType instanceof Class) && Map.class.isAssignableFrom((Class<?>) rawType);
  }
}
