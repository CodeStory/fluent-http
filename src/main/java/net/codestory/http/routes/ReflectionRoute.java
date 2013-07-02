package net.codestory.http.routes;

import java.lang.reflect.*;

class ReflectionRoute implements AnyRoute {
  private final Object resource;
  private final Method method;

  ReflectionRoute(Object resource, Method method) {
    this.resource = resource;
    this.method = method;
  }

  @Override
  public Object body(String[] params) {
    try {
      method.setAccessible(true);
      return method.invoke(resource, params);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to execute resource", e);
    }
  }
}

