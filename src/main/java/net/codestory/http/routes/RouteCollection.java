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
package net.codestory.http.routes;

import static net.codestory.http.UriParser.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.codestory.http.annotations.*;
import net.codestory.http.filters.Filter;

import com.sun.net.httpserver.*;

public class RouteCollection implements Routes {
  private final Deque<Filter> routes;
  private final Deque<Filter> filters;

  public RouteCollection() {
    this.routes = new LinkedList<>();
    this.filters = new LinkedList<>();
  }

  @Override
  public void staticDir(String fileOrClassPathDir) {
    routes.addLast(new StaticRoute(fileOrClassPathDir));
  }

  @Override
  public void add(Object resource) {
    add("", resource);
  }

  @Override
  public void add(String urlPrefix, Object resource) {
    // Hack to support Mockito spies
    Class<?> type = resource.getClass();
    if (resource.getClass().getName().contains("EnhancerByMockito")) {
      type = type.getSuperclass();
    }

    for (Method method : type.getMethods()) {
      Get annotation = method.getAnnotation(Get.class);
      if (annotation != null) {
        String uriPattern = urlPrefix + annotation.value();

        checkParametersCount(method.getParameterCount(), uriPattern);
        add(uriPattern, new ReflectionRoute(resource, method));
      }
    }
  }

  @Override
  public void get(String uriPattern, Route route) {
    checkParametersCount(0, uriPattern);
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, OneParamRoute route) {
    checkParametersCount(1, uriPattern);
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, TwoParamsRoute route) {
    checkParametersCount(2, uriPattern);
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsRoute route) {
    checkParametersCount(3, uriPattern);
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, FourParamsRoute route) {
    checkParametersCount(4, uriPattern);
    add(uriPattern, route);
  }

  @Override
  public void filter(Filter filter) {
    filters.addLast(filter);
  }

  public void reset() {
    routes.clear();
    filters.clear();
  }

  private void add(String uriPattern, AnyRoute route) {
    routes.addFirst(new RouteWrapper(uriPattern, route));
  }

  public boolean apply(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().getPath();
    System.out.println(uri);

    for (Filter filter : filters) {
      if (filter.apply(uri, exchange)) {
        return true;
      }
    }

    for (Filter route : routes) {
      if (route.apply(uri, exchange)) {
        return true;
      }
    }

    return false;
  }

  private static void checkParametersCount(int count, String uriPattern) {
    if (paramsCount(uriPattern) != count) {
      throw new IllegalArgumentException("Expected " + count + " parameters in " + uriPattern);
    }
  }
}
