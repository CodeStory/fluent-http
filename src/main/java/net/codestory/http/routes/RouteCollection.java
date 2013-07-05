package net.codestory.http.routes;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.annotations.*;
import net.codestory.http.dev.*;

import com.sun.net.httpserver.*;

public class RouteCollection implements Routes {
  private final DevMode devMode;
  private final LinkedList<RouteHolder> routes = new LinkedList<>();

  public RouteCollection(DevMode devMode) {
    this.devMode = devMode;
  }

  @Override
  public void staticDir(String fileOrClassPathDir) {
    routes.addLast(StaticRoute.forUrl(fileOrClassPathDir));
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

  private void add(String uriPattern, AnyRoute route) {
    routes.addFirst(new RouteWrapper(uriPattern, route));
  }

  public boolean apply(HttpExchange exchange) throws IOException {
    hotReloadConfigurationInDevMode();

    String uri = exchange.getRequestURI().getRawPath();

    for (RouteHolder route : routes) {
      if (route.apply(uri, exchange)) {
        return true;
      }
    }

    return false;
  }

  private void hotReloadConfigurationInDevMode() {
    Configuration lastConfiguration = devMode.getLastConfiguration();
    if (lastConfiguration != null) {
      routes.clear();
      lastConfiguration.configure(this);
    }
  }
}
