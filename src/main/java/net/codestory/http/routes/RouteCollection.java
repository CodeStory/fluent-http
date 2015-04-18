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
package net.codestory.http.routes;

import net.codestory.http.Configuration;
import net.codestory.http.Context;
import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.annotations.*;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.extensions.Extensions;
import net.codestory.http.filters.Filter;
import net.codestory.http.injection.IocAdapter;
import net.codestory.http.injection.Singletons;
import net.codestory.http.io.ClassPaths;
import net.codestory.http.io.ClasspathScanner;
import net.codestory.http.io.Resources;
import net.codestory.http.livereload.LiveReloadListener;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.security.User;
import net.codestory.http.templating.Site;
import net.codestory.http.websockets.WebSocketListener;
import net.codestory.http.websockets.WebSocketListenerFactory;
import net.codestory.http.websockets.WebSocketSession;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Stream.of;
import static net.codestory.http.annotations.AnnotationHelper.parseAnnotations;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.payload.Payload.*;
import static net.codestory.http.routes.UriParser.paramsCount;

public class RouteCollection implements Routes {
  protected final Env env;
  protected final Resources resources;
  protected final CompilerFacade compilers;
  protected final Site site;
  protected final MethodAnnotationsFactory methodAnnotationsFactory;
  protected final RouteSorter routes;
  protected final Deque<Supplier<Filter>> filters;

  protected IocAdapter iocAdapter;
  protected Extensions extensions;
  protected WebSocketListenerFactory webSocketListenerFactory;
  protected ContextToPayload contextToPayload;

  public RouteCollection(Env env) {
    this.env = env;
    this.resources = new Resources(env);
    this.compilers = new CompilerFacade(env, resources);
    this.site = new Site(env, resources);
    this.methodAnnotationsFactory = createMethodAnnotationsFactory();
    this.routes = new RouteSorter();
    this.filters = new LinkedList<>();
    this.iocAdapter = new Singletons();
    this.extensions = new Extensions() {
      // No extension
    };
    this.webSocketListenerFactory = (session, context) -> {
      throw new UnsupportedOperationException();
    };
  }

  public void configure(Configuration configuration) {
    configuration.configure(this);
    installExtensions();
    addStaticRoutes();

    contextToPayload = createContextToPayload(routes.getSortedRoutes(), filters);
  }

  private void installExtensions() {
    TypeConvert.configureOrReplaceMapper(mapper -> extensions.configureOrReplaceObjectMapper(mapper, env));
    extensions.configureCompilers(compilers, env);
  }

  private void addStaticRoutes() {
    routes.addStaticRoute(new WebJarsRoute(env.prodMode()));
    routes.addStaticRoute(new StaticRoute(env.prodMode(), resources, compilers));
    if (!env.prodMode()) {
      routes.addStaticRoute(new SourceMapRoute(resources, compilers));
      routes.addStaticRoute(new SourceRoute(resources));
    }

    if (env.liveReloadServer()) {
      get("/livereload.js", ClassPaths.getResource("livereload/livereload.js"));
      setWebSocketListenerFactory((session, context) -> new LiveReloadListener(session, env));
    }
  }

  public PayloadWriter createPayloadWriter(Request request, Response response) {
    return extensions.createPayloadWriter(request, response, env, site, resources, compilers);
  }

  public Context createContext(Request request, Response response) {
    return extensions.createContext(request, response, iocAdapter, env, site);
  }

  @Override
  public RouteCollection setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public RouteCollection setIocAdapter(IocAdapter iocAdapter) {
    this.iocAdapter = iocAdapter;
    return this;
  }

  @Override
  public Routes setWebSocketListenerFactory(WebSocketListenerFactory factory) {
    this.webSocketListenerFactory = factory;
    return null;
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

  protected void addResource(String urlPrefix, Class<?> resourceType, Supplier<Object> resource) {
    parseAnnotations(urlPrefix, resourceType, (httpMethod, uri, method) -> addResource(httpMethod, method, resource, uri));
  }

  protected void addResource(String httpMethod, Method method, Supplier<Object> resource, String uriPattern) {
    int methodParamsCount = method.getParameterCount();
    int uriParamsCount = paramsCount(uriPattern);
    if (methodParamsCount < uriParamsCount) {
      throw new IllegalArgumentException("Expected at least " + uriParamsCount + " parameters in " + uriPattern);
    }

    add(httpMethod, uriPattern, new ReflectionRoute(resource, method, methodAnnotationsFactory.forMethod(method)));
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
  public Routes anyGet(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(GET, route));
    return this;
  }

  @Override
  public Routes anyHead(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(HEAD, route));
    return this;
  }

  @Override
  public Routes anyPost(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(POST, route));
    return this;
  }

  @Override
  public Routes anyPut(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(PUT, route));
    return this;
  }

  @Override
  public Routes anyOptions(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(OPTIONS, route));
    return this;
  }

  @Override
  public Routes anyDelete(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(DELETE, route));
    return this;
  }

  @Override
  public RouteCollection any(NoParamRouteWithContext route) {
    routes.addCatchAllRoute(new CatchAllRoute(route));
    return this;
  }

  @Override
  public RouteCollection autoDiscover(String packageToScan) {
    Set<Class<?>> types = new ClasspathScanner().getTypesAnnotatedWith(packageToScan, Resource.class);
    types.forEach(this::add);
    return this;
  }

  @Override
  public Routes bind(String uriRoot, File path) {
    routes.addStaticRoute(new BoundFolderRoute(uriRoot, path));
    return this;
  }

  @Override
  public <T extends Annotation> Routes registerAroundAnnotation(Class<T> annotationType, ApplyAroundAnnotation<T> apply) {
    methodAnnotationsFactory.registerAroundAnnotation(annotationType, () -> apply);
    return this;
  }

  @Override
  public <T extends Annotation> Routes registerAroundAnnotation(Class<T> annotationType, Class<? extends ApplyAroundAnnotation<T>> applyType) {
    methodAnnotationsFactory.registerAroundAnnotation(annotationType, () -> (ApplyAroundAnnotation<T>) iocAdapter.get(applyType));
    return this;
  }

  @Override
  public <T extends Annotation> Routes registerAfterAnnotation(Class<T> annotationType, ApplyAfterAnnotation<T> apply) {
    methodAnnotationsFactory.registerAfterAnnotation(annotationType, () -> apply);
    return this;
  }

  @Override
  public <T extends Annotation> Routes registerAfterAnnotation(Class<T> annotationType, Class<? extends ApplyAfterAnnotation<T>> applyType) {
    methodAnnotationsFactory.registerAfterAnnotation(annotationType, () -> (ApplyAfterAnnotation<T>) iocAdapter.get(applyType));
    return this;
  }

  @Override
  public RoutesWithPattern url(String uriPattern) {
    return new RoutesWithPattern(this, uriPattern);
  }

  protected RouteCollection add(String method, String uriPattern, AnyRoute route) {
    routes.addUserRoute(new RouteWithPattern(method, uriPattern, route));
    return this;
  }

  public WebSocketListener createWebSocketListener(WebSocketSession session, Request request, Response response) {
    Context context = createContext(request, response);

    return webSocketListenerFactory.create(session, context);
  }

  public Payload apply(Request request, Response response) throws Exception {
    Context context = createContext(request, response);
    if (context.uri() == null) {
      return Payload.notFound();
    }

    return contextToPayload.get(context.uri(), context);
  }

  private static ContextToPayload createContextToPayload(Route[] sortedRoutes, Deque<Supplier<Filter>> filters) {
    ContextToPayload payloadSupplier = (uri, context) -> {
      Payload response = notFound();

      for (Route route : sortedRoutes) {
        if (route.matchUri(uri)) {
          if (route.matchMethod(context.method())) {
            return route.apply(uri, context);
          }
          response = methodNotAllowed();
        }
      }

      return response;
    };

    for (Supplier<Filter> filterSupplier : filters) {
      Filter filter = filterSupplier.get();

      ContextToPayload nextFilter = payloadSupplier;

      payloadSupplier = (uri, context) -> {
        if (filter.matches(uri, context)) {
          return filter.apply(uri, context, () -> nextFilter.get(uri, context));
        } else {
          return nextFilter.get(uri, context);
        }
      };
    }

    return payloadSupplier;
  }

  protected MethodAnnotationsFactory createMethodAnnotationsFactory() {
    MethodAnnotationsFactory factory = new MethodAnnotationsFactory();

    factory.registerAroundAnnotation(Roles.class, () -> (roles, context, payloadSupplier) -> isAuthorized(roles, context.currentUser()) ? payloadSupplier.apply(context) : Payload.forbidden());
    factory.registerAfterAnnotation(AllowOrigin.class, () -> (origin, context, payload) -> payload.withAllowOrigin(origin.value()));
    factory.registerAfterAnnotation(AllowMethods.class, () -> (methods, context, payload) -> payload.withAllowMethods(methods.value()));
    factory.registerAfterAnnotation(AllowCredentials.class, () -> (credentials, context, payload) -> payload.withAllowCredentials(credentials.value()));
    factory.registerAfterAnnotation(AllowHeaders.class, () -> (allowedHeaders, context, payload) -> payload.withAllowHeaders(allowedHeaders.value()));
    factory.registerAfterAnnotation(ExposeHeaders.class, () -> (exposedHeaders, context, payload) -> payload.withExposeHeaders(exposedHeaders.value()));
    factory.registerAfterAnnotation(MaxAge.class, () -> (maxAge, context, payload) -> payload.withMaxAge(maxAge.value()));

    return factory;
  }

  protected boolean isAuthorized(Roles roles, User user) {
    if (roles.allMatch()) {
      return of(roles.value()).allMatch(role -> user.isInRole(role));
    } else {
      return of(roles.value()).anyMatch(role -> user.isInRole(role));
    }
  }

  protected String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
