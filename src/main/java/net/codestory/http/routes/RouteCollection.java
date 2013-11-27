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

import static net.codestory.http.internal.UriParser.*;
import static net.codestory.http.routes.Match.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;
import net.codestory.http.payload.*;

import org.simpleframework.http.*;

public class RouteCollection implements Routes {
  private final List<Route> routes;
  private final List<Supplier<Filter>> filters;
  private IocAdapter iocAdapter = new Singletons();

  public RouteCollection() {
    this.routes = new LinkedList<>();
    this.filters = new LinkedList<>();
  }

  public void setIocAdapter(IocAdapter iocAdapter) {
    this.iocAdapter = iocAdapter;
  }

  @Override
  public void include(Class<? extends Configuration> configurationClass) {
    iocAdapter.get(configurationClass).configure(this);
  }

  @Override
  public void include(Configuration configuration) {
    configuration.configure(this);
  }

  @Override
  public void filter(Class<? extends Filter> filterClass) {
    filters.add(() -> iocAdapter.get(filterClass));
  }

  @Override
  public void filter(Filter filter) {
    filters.add(() -> filter);
  }

  @Override
  public void add(Class<?> resourceType) {
    addResource("", resourceType, () -> iocAdapter.get(resourceType));
  }

  @Override
  public void add(String urlPrefix, Class<?> resourceType) {
    addResource(urlPrefix, resourceType, () -> iocAdapter.get(resourceType));
  }

  @Override
  public void add(Object resource) {
    addResource("", resource.getClass(), () -> resource);
  }

  @Override
  public void add(String urlPrefix, Object resource) {
    addResource(urlPrefix, resource.getClass(), () -> resource);
  }

  private void addResource(String urlPrefix, Class<?> type, Supplier<Object> resource) {
    // Hack to support Mockito Spies
    if (type.getName().contains("EnhancerByMockito")) {
      type = type.getSuperclass();
    }

    for (Method method : type.getMethods()) {
      for (Get get : method.getDeclaredAnnotationsByType(Get.class)) {
        addResource("GET", method, resource, urlPrefix + get.value());
      }
      for (Post post : method.getDeclaredAnnotationsByType(Post.class)) {
        addResource("POST", method, resource, urlPrefix + post.value());
      }
      for (Put put : method.getDeclaredAnnotationsByType(Put.class)) {
        addResource("PUT", method, resource, urlPrefix + put.value());
      }
    }
  }

  private void addResource(String httpMethod, Method method, Supplier<Object> resource, String uriPattern) {
    int methodParamsCount = method.getParameterCount();
    int uriParamsCount = paramsCount(uriPattern);

    if (methodParamsCount == uriParamsCount) {
      add(httpMethod, checkParametersCount(uriPattern, methodParamsCount), new ReflectionRoute(resource, method));
    } else if (methodParamsCount == (uriParamsCount + 1)) {
      add(httpMethod, checkParametersCount(uriPattern, methodParamsCount - 1), new ReflectionRouteWithContext(resource, method));
    } else {
      throw new IllegalArgumentException("Expected " + uriParamsCount + " or " + (uriParamsCount + 1) + " parameters in " + uriPattern);
    }
  }

  @Override
  public void get(String uriPattern, Object payload) {
    get(uriPattern, () -> payload);
  }

  @Override
  public void get(String uriPattern, NoParamRoute route) {
    add("GET", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void get(String uriPattern, OneParamRoute route) {
    add("GET", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void get(String uriPattern, TwoParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void get(String uriPattern, FourParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void get(String uriPattern, NoParamRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void get(String uriPattern, OneParamRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void get(String uriPattern, TwoParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void get(String uriPattern, FourParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void post(String uriPattern, NoParamRoute route) {
    add("POST", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void post(String uriPattern, OneParamRoute route) {
    add("POST", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void post(String uriPattern, TwoParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void post(String uriPattern, ThreeParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void post(String uriPattern, FourParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void post(String uriPattern, NoParamRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void post(String uriPattern, OneParamRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void post(String uriPattern, TwoParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void post(String uriPattern, ThreeParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void post(String uriPattern, FourParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void put(String uriPattern, NoParamRoute route) {
    add("PUT", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void put(String uriPattern, OneParamRoute route) {
    add("PUT", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void put(String uriPattern, TwoParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void put(String uriPattern, ThreeParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void put(String uriPattern, FourParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void put(String uriPattern, NoParamRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 0), route);
  }

  @Override
  public void put(String uriPattern, OneParamRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 1), route);
  }

  @Override
  public void put(String uriPattern, TwoParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 2), route);
  }

  @Override
  public void put(String uriPattern, ThreeParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 3), route);
  }

  @Override
  public void put(String uriPattern, FourParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 4), route);
  }

  @Override
  public void catchAll(Object payload) {
    catchAll(() -> payload);
  }

  @Override
  public void catchAll(NoParamRoute route) {
    routes.add(new CatchAllRoute(route));
  }

  @Override
  public void catchAll(NoParamRouteWithContext route) {
    routes.add(new CatchAllRouteWithContext(route));
  }

  private void add(String method, String uriPattern, AnyRoute route) {
    routes.add(new RouteWrapper(method, uriPattern, route));
  }

  private void add(String method, String uriPattern, AnyRouteWithContext route) {
    routes.add(new RouteWithContextWrapper(method, uriPattern, route));
  }

  // TEMP
  public void addStaticRoutes(boolean cache) {
    routes.add(cache ? new CachedStaticRoute() : new StaticRoute());
    routes.add(new SourceMapRoute());
  }

  public Match apply(Request request, Response response) throws IOException {
    String uri = request.getPath().getPath();
    if (uri == null) {
      return WRONG_URL;
    }

    for (Supplier<Filter> filter : filters) {
      if (filter.get().apply(uri, request, response)) {
        return OK;
      }
    }

    Match bestMatch = WRONG_URL;

    for (Route route : routes) {
      Match match = route.apply(uri, request, response);
      if (match == OK) {
        return OK;
      }
      if (match.isBetter(bestMatch)) {
        bestMatch = match;
      }
    }

    if (bestMatch == TRY_WITH_LEADING_SLASH) {
      Payload.seeOther(uri + "/").writeTo(response);
      return OK;
    }

    return bestMatch;
  }

  private static String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
