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
import net.codestory.http.annotations.Resource;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.extensions.Extensions;
import net.codestory.http.filters.Filter;
import net.codestory.http.filters.PayloadSupplier;
import net.codestory.http.injection.IocAdapter;
import net.codestory.http.injection.Singletons;
import net.codestory.http.io.ClassPaths;
import net.codestory.http.io.Resources;
import net.codestory.http.livereload.LiveReloadListener;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.templating.Site;
import net.codestory.http.websockets.*;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

import static net.codestory.http.annotations.AnnotationHelper.parseAnnotations;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.payload.Payload.*;
import static net.codestory.http.routes.UriParser.paramsCount;

public class RouteCollection implements Routes {
  protected final Env env;
  protected final Resources resources;
  protected final CompilerFacade compilers;
  protected final Site site;
  protected final RouteSorter routes;
  protected final Deque<Supplier<Filter>> filters;

  protected IocAdapter iocAdapter;
  protected Extensions extensions;
  protected Route[] sortedRoutes;
  protected WebSocketListenerFactory webSocketListenerFactory;

  public RouteCollection(Env env) {
    this.env = env;
    this.resources = new Resources(env);
    this.compilers = new CompilerFacade(env, resources);
    this.site = new Site(env, resources);
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

    sortedRoutes = routes.getSortedRoutes();
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

  protected void addResource(String urlPrefix, Class<?> type, Supplier<Object> resource) {
    parseAnnotations(urlPrefix, type, (httpMethod, uri, method) -> addResource(httpMethod, method, resource, uri));
  }

  protected void addResource(String httpMethod, Method method, Supplier<Object> resource, String uriPattern) {
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
  public RoutesWithPattern url(String uriPattern) {
    return new RoutesWithPattern(this, uriPattern);
  }

  @Override
  public void autoDiscover(String packageToScan) {
    final Reflections reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.forPackage(packageToScan))
      .addScanners(new TypeAnnotationsScanner()));
    final Set<Class<?>> resources = reflections.getTypesAnnotatedWith(Resource.class);
    resources.forEach(this::add);
  }

  protected RouteCollection add(String method, String uriPattern, AnyRoute route) {
    routes.addUserRoute(new RouteWithPattern(method, uriPattern, route));
    return this;
  }

  public WebSocketListener createWebSocketListener(WebSocketSession session, Context context) {
    return webSocketListenerFactory.create(session, context);
  }

  public Payload apply(Context context) throws Exception {
    String uri = context.uri();
    if (uri == null) {
      return notFound();
    }

    PayloadSupplier payloadSupplier = () -> {
      Payload response = notFound();

      for (Route route : sortedRoutes) {
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

    for (Supplier<Filter> filterSupplier : filters) {
      Filter filter = filterSupplier.get();

      if (filter.matches(uri, context)) {
        PayloadSupplier nextFilter = payloadSupplier;
        payloadSupplier = () -> filter.apply(uri, context, nextFilter);
      }
    }

    return payloadSupplier.get();
  }

  protected String checkParametersCount(String uriPattern, int count) {
    if (paramsCount(uriPattern) != count) {
      String error = (count == 1) ? "1 parameter" : count + " parameters";
      throw new IllegalArgumentException("Expected " + error + " in " + uriPattern);
    }
    return uriPattern;
  }
}
