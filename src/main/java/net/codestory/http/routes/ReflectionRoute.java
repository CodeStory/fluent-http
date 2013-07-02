package net.codestory.http.routes;

import java.lang.reflect.*;

class ReflectionRoute implements AnyRoute {
  private final Object resource;
  private final Method method;
  private final Class<?>[] parameterTypes;

  ReflectionRoute(Object resource, Method method) {
    this.resource = resource;
    this.method = method;
    this.parameterTypes = method.getParameterTypes();
  }

  @Override
  public Object body(String[] params) {
    try {
      method.setAccessible(true);
      return method.invoke(resource, convert(params, parameterTypes));
    } catch (Exception e) {
      throw new IllegalStateException("Unable to execute resource", e);
    }
  }

  private static Object[] convert(String[] params, Class<?>[] parameterTypes) {
    Object[] converted = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      converted[i] = convert(params[i], parameterTypes[i]);
    }
    return converted;
  }

  static Object convert(String param, Class<?> type) {
    if (type == int.class) {
      return Integer.parseInt(param);
    } else if (type == Integer.class) {
      return Integer.valueOf(param);
    } else if (type == boolean.class) {
      return Boolean.parseBoolean(param);
    } else if (type == Boolean.class) {
      return Boolean.valueOf(param);
    }

    return param;
  }
}

