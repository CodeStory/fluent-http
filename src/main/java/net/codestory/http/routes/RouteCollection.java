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
import static net.codestory.http.routes.Match.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.filters.*;

import org.simpleframework.http.*;

public class RouteCollection implements Routes {
  private final Deque<Route> routes;
  private final Deque<Filter> filters;

  public RouteCollection() {
    this.routes = new LinkedList<>();
    this.filters = new LinkedList<>();
  }

  @Override
  public void add(Object resource) {
    add("", resource);
  }

  @Override
  public void add(String urlPrefix, Object resource) {
    // Hack to support Mockito Spies
    Class<?> type = resource.getClass();
    if (resource.getClass().getName().contains("EnhancerByMockito")) {
      type = type.getSuperclass();
    }

    for (Method method : type.getMethods()) {
      int parameterCount = method.getParameterCount();
      Class<?>[] parameterTypes = method.getParameterTypes();

      for (Get get : method.getDeclaredAnnotationsByType(Get.class)) {
        String uriPattern = urlPrefix + get.value();

        add("GET", checkParametersCount(uriPattern, parameterCount), new ReflectionGetRoute(resource, method));
      }

      for (Post post : method.getDeclaredAnnotationsByType(Post.class)) {
        String uriPattern = urlPrefix + post.value();

        if ((parameterTypes.length == 0) || !parameterTypes[0].isAssignableFrom(Map.class)) {
          add("POST", checkParametersCount(uriPattern, parameterCount), new ReflectionGetRoute(resource, method));
        } else {
          add("POST", checkParametersCount(uriPattern, parameterCount - 1), new ReflectionPostRoute(resource, method));
        }
      }
    }
  }

  @Override
  public void get(String uriPattern, Payload payload) {
    get(uriPattern, () -> payload);
  }

  @Override
  public void get(String uriPattern, NoParamGetRoute route) {
    add("GET", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void get(String uriPattern, OneParamGetRoute route) {
    add("GET", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void get(String uriPattern, TwoParamsGetRoute route) {
    add("GET", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsGetRoute route) {
    add("GET", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void get(String uriPattern, FourParamsGetRoute route) {
    add("GET", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void post(String uriPattern, NoParamPostRoute route) {
    add("POST", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void post(String uriPattern, OneParamPostRoute route) {
    add("POST", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void post(String uriPattern, TwoParamsPostRoute route) {
    add("POST", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void post(String uriPattern, ThreeParamsPostRoute route) {
    add("POST", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void post(String uriPattern, FourParamsPostRoute route) {
    add("POST", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void filter(Filter filter) {
    filters.addLast(filter);
  }

  public void reset() {
    routes.clear();
    filters.clear();
  }

  private void add(String method, String uriPattern, AnyGetRoute route) {
    routes.addFirst(new GetRouteWrapper(method, uriPattern, route));
  }

  private void add(String method, String uriPattern, AnyPostRoute route) {
    routes.addFirst(new PostRouteWrapper(method, uriPattern, route));
  }

  public Match apply(Request request, Response response) throws IOException {
    String uri = request.getPath().getPath();
    if (uri == null) {
      return WRONG_URL;
    }

    for (Filter filter : filters) {
      if (filter.apply(uri, request, response)) {
        return OK;
      }
    }

    Match bestMatch = WRONG_URL;

    List<Route> allRoutes = new ArrayList<>();
    allRoutes.addAll(routes);
    allRoutes.add(new StaticRoute());

    for (Route route : allRoutes) {
      Match match = route.apply(uri, request, response);
      if (match == OK) {
        return OK;
      }
      if (match.isBetter(bestMatch)) {
        bestMatch = match;
      }
    }

    return bestMatch;
  }

  private static String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      throw new IllegalArgumentException("Expected " + count + " parameters in " + uriPattern);
    }
    return uriPattern;
  }
}
