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

import static java.util.Arrays.*;
import static net.codestory.http.internal.UriParser.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;
import net.codestory.http.internal.*;
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

  public RouteCollection setIocAdapter(IocAdapter iocAdapter) {
    this.iocAdapter = iocAdapter;
    return this;
  }

  @Override
  public RouteCollection include(Class<? extends Configuration> configurationClass) {
    iocAdapter.get(configurationClass).configure(this);
    return this;
  }

  @Override
  public RouteCollection include(Configuration configuration) {
    configuration.configure(this);
    return this;
  }

  @Override
  public RouteCollection filter(Class<? extends Filter> filterClass) {
    filters.add(() -> iocAdapter.get(filterClass));
    return this;
  }

  @Override
  public RouteCollection filter(Filter filter) {
    filters.add(() -> filter);
    return this;
  }

  @Override
  public RouteCollection add(Class<?> resourceType) {
    addResource("", resourceType, () -> iocAdapter.get(resourceType));
    return this;
  }

  @Override
  public RouteCollection add(String urlPrefix, Class<?> resourceType) {
    addResource(urlPrefix, resourceType, () -> iocAdapter.get(resourceType));
    return this;
  }

  @Override
  public RouteCollection add(Object resource) {
    addResource("", resource.getClass(), () -> resource);
    return this;
  }

  @Override
  public RouteCollection add(String urlPrefix, Object resource) {
    addResource(urlPrefix, resource.getClass(), () -> resource);
    return this;
  }

  private void addResource(String urlPrefix, Class<?> type, Supplier<Object> resource) {
    // Hack to support Mockito Spies
    if (type.getName().contains("EnhancerByMockito")) {
      type = type.getSuperclass();
    }

    for (Method method : type.getMethods()) {
      stream(method.getAnnotationsByType(Get.class)).forEach(get -> addResource("GET", method, resource, urlPrefix + get.value()));
      stream(method.getAnnotationsByType(Post.class)).forEach(post -> addResource("POST", method, resource, urlPrefix + post.value()));
      stream(method.getAnnotationsByType(Put.class)).forEach(put -> addResource("PUT", method, resource, urlPrefix + put.value()));
      stream(method.getAnnotationsByType(Delete.class)).forEach(delete -> addResource("DELETE", method, resource, urlPrefix + delete.value()));
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
  public RouteCollection get(String uriPattern, Object payload) {
    get(uriPattern, () -> payload);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, NoParamRoute route) {
    add("GET", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, OneParamRoute route) {
    add("GET", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, TwoParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, ThreeParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, FourParamsRoute route) {
    add("GET", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, NoParamRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, OneParamRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, TwoParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, ThreeParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, FourParamsRouteWithContext route) {
    add("GET", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, NoParamRoute route) {
    add("POST", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, OneParamRoute route) {
    add("POST", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, TwoParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, ThreeParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, FourParamsRoute route) {
    add("POST", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, NoParamRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, OneParamRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, TwoParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, ThreeParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, FourParamsRouteWithContext route) {
    add("POST", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, NoParamRoute route) {
    add("PUT", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, OneParamRoute route) {
    add("PUT", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, TwoParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, ThreeParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, FourParamsRoute route) {
    add("PUT", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, NoParamRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, OneParamRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, TwoParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, ThreeParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, FourParamsRouteWithContext route) {
    add("PUT", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, NoParamRoute route) {
    add("DELETE", checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, OneParamRoute route) {
    add("DELETE", checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, TwoParamsRoute route) {
    add("DELETE", checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, ThreeParamsRoute route) {
    add("DELETE", checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, FourParamsRoute route) {
    add("DELETE", checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection catchAll(Object payload) {
    catchAll(() -> payload);
    return this;
  }

  @Override
  public RouteCollection catchAll(NoParamRoute route) {
    routes.add(new CatchAllRoute(route));
    return this;
  }

  @Override
  public RouteCollection catchAll(NoParamRouteWithContext route) {
    routes.add(new CatchAllRouteWithContext(route));
    return this;
  }

  private RouteCollection add(String method, String uriPattern, AnyRoute route) {
    routes.add(new RouteWrapper(method, uriPattern, route));
    return this;
  }

  private RouteCollection add(String method, String uriPattern, AnyRouteWithContext route) {
    routes.add(new RouteWithContextWrapper(method, uriPattern, route));
    return this;
  }

  // TEMP
  public void addStaticRoutes(boolean cache) {
    routes.add(cache ? new CachedStaticRoute() : new StaticRoute());
    routes.add(new SourceMapRoute());
  }

  public Payload apply(Request request, Response response) throws IOException {
    String uri = request.getPath().getPath();
    if (uri == null) {
      return Payload.notFound();
    }

    final Context context = new Context(uri, request, response);

    PayloadSupplier payloadSupplier = () -> {
      Payload bestMatch = Payload.notFound();

      for (Route route : routes) {
        Payload match = route.apply(uri, context);
        if (!match.isError()) {
          return match;
        }

        if (match.isBetter(bestMatch)) {
          bestMatch = match;
        }
      }

      return bestMatch;
    };

    for (Supplier<Filter> filter : filters) {
      PayloadSupplier nextFilter = payloadSupplier;
      payloadSupplier = () -> filter.get().apply(uri, context, nextFilter);
    }

    return payloadSupplier.get();
  }

  private static String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
