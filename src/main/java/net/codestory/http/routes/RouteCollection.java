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
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.internal.UriParser.*;
import static net.codestory.http.misc.ForEach.forEach;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;

public class RouteCollection implements Routes {
  private final List<Route> routes;
  private final LinkedList<Supplier<Filter>> filters;
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

    Prefix prefixAnnotation = type.getAnnotation(Prefix.class);
    String classPrefix = (prefixAnnotation != null) ? prefixAnnotation.value() : "";

    String prefix = urlPrefix + classPrefix;

    for (Method method : type.getMethods()) {
      forEach(method.getAnnotationsByType(Get.class)).then(get -> addResource(GET, method, resource, prefix + get.value()));
      forEach(method.getAnnotationsByType(Post.class)).then(post -> addResource(POST, method, resource, prefix + post.value()));
      forEach(method.getAnnotationsByType(Put.class)).then(put -> addResource(PUT, method, resource, prefix + put.value()));
      forEach(method.getAnnotationsByType(Delete.class)).then(delete -> addResource(DELETE, method, resource, prefix + delete.value()));
      forEach(method.getAnnotationsByType(Head.class)).then(delete -> addResource(HEAD, method, resource, prefix + delete.value()));
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
    add(GET, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, NoParamRouteWithContext route) {
    add(GET, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, OneParamRoute route) {
    add(GET, checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, TwoParamsRoute route) {
    add(GET, checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, ThreeParamsRoute route) {
    add(GET, checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection get(String uriPattern, FourParamsRoute route) {
    add(GET, checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, Object payload) {
    head(uriPattern, () -> payload);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, NoParamRoute route) {
    add(HEAD, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, NoParamRouteWithContext route) {
    add(HEAD, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, OneParamRoute route) {
    add(HEAD, checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, TwoParamsRoute route) {
    add(HEAD, checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, ThreeParamsRoute route) {
    add(HEAD, checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection head(String uriPattern, FourParamsRoute route) {
    add(HEAD, checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, NoParamRoute route) {
    add(POST, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, NoParamRouteWithContext route) {
    add(POST, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, OneParamRoute route) {
    add(POST, checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, TwoParamsRoute route) {
    add(POST, checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, ThreeParamsRoute route) {
    add(POST, checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection post(String uriPattern, FourParamsRoute route) {
    add(POST, checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, NoParamRoute route) {
    add(PUT, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, NoParamRouteWithContext route) {
    add(PUT, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, OneParamRoute route) {
    add(PUT, checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, TwoParamsRoute route) {
    add(PUT, checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, ThreeParamsRoute route) {
    add(PUT, checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection put(String uriPattern, FourParamsRoute route) {
    add(PUT, checkParametersCount(uriPattern, 4), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, NoParamRoute route) {
    add(DELETE, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, NoParamRouteWithContext route) {
    add(DELETE, checkParametersCount(uriPattern, 0), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, OneParamRoute route) {
    add(DELETE, checkParametersCount(uriPattern, 1), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, TwoParamsRoute route) {
    add(DELETE, checkParametersCount(uriPattern, 2), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, ThreeParamsRoute route) {
    add(DELETE, checkParametersCount(uriPattern, 3), route);
    return this;
  }

  @Override
  public RouteCollection delete(String uriPattern, FourParamsRoute route) {
    add(DELETE, checkParametersCount(uriPattern, 4), route);
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
    routes.add(new CatchAllRoute(route));
    return this;
  }

  @Override
  public RoutesWithPattern with(String uriPattern) {
    return new RoutesWithPattern(this, uriPattern);
  }

  private RouteCollection add(String method, String uriPattern, AnyRoute route) {
    routes.add(new RouteWrapper(method, uriPattern, route));
    return this;
  }

  // TEMP
  public void addStaticRoutes() {
    boolean prodMode = Env.INSTANCE.prodMode();

    routes.add(new WebJarsRoute(prodMode));
    routes.add(new StaticRoute(prodMode));
    routes.add(new SourceMapRoute());
  }

  public Payload apply(Context context) throws IOException {
    String uri = context.uri();
    if (uri == null) {
      return Payload.notFound();
    }

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

    for (Iterator<Supplier<Filter>> ite = filters.descendingIterator();ite.hasNext();) {
      Supplier<Filter>  filter = ite.next();
      PayloadSupplier nextFilter = payloadSupplier;
      payloadSupplier = () -> filter.get().apply(uri, context, nextFilter);
    }

    return payloadSupplier.get();
  }

  public IocAdapter getIocAdapter() {
    return iocAdapter;
  }

  private static String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
