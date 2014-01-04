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
import java.util.function.*;

import net.codestory.http.annotations.*;
import net.codestory.http.payload.*;

class AbstractReflectionRoute {
  protected final Supplier<Object> resource;
  protected final Method method;
  protected final String contentType;

  protected AbstractReflectionRoute(Supplier<Object> resource, Method method) {
    this.resource = resource;
    this.method = method;
    this.contentType = findContentType(method);
  }

  protected Object payload(Object[] arguments) throws Throwable {
    Object target = resource.get();

    Object response = invoke(method, target, arguments);
    Object payload = emptyIfNull(response);

    return new Payload(contentType, payload);
  }

  private static Object invoke(Method method, Object target, Object[] arguments) throws Throwable {
    method.setAccessible(true);

    try {
      return method.invoke(target, arguments);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  private static Object emptyIfNull(Object payload) {
    return (payload == null) ? "" : payload;
  }

  private static String findContentType(Method method) {
    Produces annotation = method.getAnnotation(Produces.class);
    return (annotation == null) ? null : annotation.value();
  }
}

