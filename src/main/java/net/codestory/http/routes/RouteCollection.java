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

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.dev.*;
import net.codestory.http.filters.Filter;

import com.sun.net.httpserver.*;

public class RouteCollection implements Routes {
  private final DevMode devMode;
  private final LinkedList<RouteHolder> routes;
  private final LinkedList<Filter> filters;

  public RouteCollection(DevMode devMode) {
    this.devMode = devMode;
    this.routes = new LinkedList<>();
    this.filters = new LinkedList<>();
  }

  @Override
  public void staticDir(String fileOrClassPathDir) {
    routes.addLast(new StaticRoute(Paths.get(fileOrClassPathDir)));
  }

  @Override
  public void add(Object resource) {
    add("", resource);
  }

  @Override
  public void add(String urlPrefix, Object resource) {
    for (Method method : resource.getClass().getDeclaredMethods()) {
      Get annotation = method.getAnnotation(Get.class);
      if (annotation != null) {
        add(urlPrefix + annotation.value(), new ReflectionRoute(resource, method));
      }
    }
  }

  @Override
  public void get(String uriPattern, Route route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, OneParamRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, TwoParamsRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, ThreeParamsRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void get(String uriPattern, FourParamsRoute route) {
    add(uriPattern, route);
  }

  @Override
  public void filter(Filter filter) {
    filters.addLast(filter);
  }

  private void add(String uriPattern, AnyRoute route) {
    routes.addFirst(new RouteWrapper(uriPattern, route));
  }

  public boolean apply(HttpExchange exchange) throws IOException {
    hotReloadConfigurationInDevMode();

    String uri = URLDecoder.decode(exchange.getRequestURI().getRawPath(), "US-ASCII");

    for (Filter filter : filters) {
      if (filter.apply(uri, exchange)) {
        return true;
      }
    }

    for (RouteHolder route : routes) {
      if (route.apply(uri, exchange)) {
        return true;
      }
    }

    return false;
  }

  private void hotReloadConfigurationInDevMode() {
    Configuration lastConfiguration = devMode.getLastConfiguration();
    if (lastConfiguration == null) {
      return;
    }

    routes.clear();
    filters.clear();
    lastConfiguration.configure(this);
  }
}
