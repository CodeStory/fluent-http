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

import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.misc.ForEach.*;
import static net.codestory.http.payload.Payload.*;
import static net.codestory.http.routes.UriParser.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.extensions.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.templating.*;

public class RouteCollection implements Routes {
  private final Env env;
  private final Site site;
  private final Deque<Route> routes;
  private final Deque<Supplier<Filter>> filters;

  private IocAdapter iocAdapter;
  private ContextFactory contextFactory;
  private PayloadWriterFactory payloadWriterFactory;

  public RouteCollection() {
    this.site = new Site();
    this.env = new Env();
    this.routes = new LinkedList<>();
    this.filters = new LinkedList<>();
    this.iocAdapter = new Singletons();
    this.contextFactory = (request, response) -> new Context(request, response, iocAdapter);
    this.payloadWriterFactory = (request, response) -> new PayloadWriter(env, site, request, response);
  }

  @Override
  public RouteCollection setIocAdapter(IocAdapter iocAdapter) {
    this.iocAdapter = iocAdapter;
    return this;
  }

  @Override
  public Routes setContextFactory(ContextFactory contextFactory) {
    this.contextFactory = contextFactory;
    return this;
  }

  @Override
  public Routes setPayloadWriterFactory(PayloadWriterFactory payloadWriterFactory) {
    this.payloadWriterFactory = payloadWriterFactory;
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
    filters.addFirst(() -> iocAdapter.get(filterClass));
    return this;
  }

  @Override
  public RouteCollection filter(Filter filter) {
    filters.addFirst(() -> filter);
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

    for (Method method : type.getMethods()) {
      forEach(method.getAnnotationsByType(Get.class)).then(get -> addResource(GET, method, resource, url(urlPrefix, classPrefix, get.value())));
      forEach(method.getAnnotationsByType(Post.class)).then(post -> addResource(POST, method, resource, url(urlPrefix, classPrefix, post.value())));
      forEach(method.getAnnotationsByType(Put.class)).then(put -> addResource(PUT, method, resource, url(urlPrefix, classPrefix, put.value())));
      forEach(method.getAnnotationsByType(Delete.class)).then(delete -> addResource(DELETE, method, resource, url(urlPrefix, classPrefix, delete.value())));
      forEach(method.getAnnotationsByType(Head.class)).then(head -> addResource(HEAD, method, resource, url(urlPrefix, classPrefix, head.value())));
      forEach(method.getAnnotationsByType(Options.class)).then(opts -> addResource(OPTIONS, method, resource, url(urlPrefix, classPrefix, opts.value())));
    }
  }

  static String url(String resourcePrefix, String classPrefix, String uri) {
    return new UrlConcat().url(resourcePrefix, classPrefix, uri);
  }

  private void addResource(String httpMethod, Method method, Supplier<Object> resource, String uriPattern) {
    int methodParamsCount = method.getParameterCount();
    int uriParamsCount = paramsCount(uriPattern);
    if (methodParamsCount < uriParamsCount) {
      throw new IllegalArgumentException("Expected at least" + uriParamsCount + " parameters in " + uriPattern);
    }

    add(httpMethod, uriPattern, new ReflectionRoute(resource, method));
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
  public RouteCollection options(String uriPattern, Object payload) {
      options(uriPattern, () -> payload);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, NoParamRoute route) {
      add(OPTIONS, checkParametersCount(uriPattern, 0), route);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, NoParamRouteWithContext route) {
      add(OPTIONS, checkParametersCount(uriPattern, 0), route);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, OneParamRoute route) {
      add(OPTIONS, checkParametersCount(uriPattern, 1), route);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, TwoParamsRoute route) {
      add(OPTIONS, checkParametersCount(uriPattern, 2), route);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, ThreeParamsRoute route) {
      add(OPTIONS, checkParametersCount(uriPattern, 3), route);
      return this;
  }

  @Override
  public RouteCollection options(String uriPattern, FourParamsRoute route) {
      add(OPTIONS, checkParametersCount(uriPattern, 4), route);
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

  public void addStaticRoutes(boolean prodMode) {
    routes.add(new WebJarsRoute(prodMode));
    routes.add(new StaticRoute(prodMode));
    routes.add(new SourceMapRoute());
  }

  public Payload apply(Context context) throws IOException {
    String uri = context.uri();
    if (uri == null) {
      return notFound();
    }

    PayloadSupplier payloadSupplier = () -> {
      Payload response = notFound();

      for (Route route : routes) {
        if (route.matchUri(uri)) {
          if (route.matchMethod(context.method())) {
            return route.apply(uri, context);
          }
          response = methodNotAllowed();
        } else if (!uri.endsWith("/") && route.matchUri(uri + '/')) {
          if (route.matchMethod(context.method())) {
            return seeOther(uri + '/');
          }
          response = methodNotAllowed();
        }
      }

      return response;
    };

    for (Supplier<Filter> filter : filters) {
      if (filter.get().matches(uri, context)) {
        PayloadSupplier nextFilter = payloadSupplier;
        payloadSupplier = () -> filter.get().apply(uri, context, nextFilter);
      }
    }

    return payloadSupplier.get();
  }

  public Context createContext(Request request, Response response) {
    return contextFactory.create(request, response);
  }

  public PayloadWriter createPayloadWriter(Request request, Response response) {
    return payloadWriterFactory.create(request, response);
  }

  private static String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
