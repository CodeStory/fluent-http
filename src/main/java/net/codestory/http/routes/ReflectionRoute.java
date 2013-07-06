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
    if ((type == int.class) || (type == Integer.class)) {
      return Integer.parseInt(param);
    } else if ((type == boolean.class) || (type == Boolean.class)) {
      return Boolean.parseBoolean(param);
    }

    return param;
  }
}

