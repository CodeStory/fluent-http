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
import net.codestory.http.convert.*;
import net.codestory.http.exchange.*;
import net.codestory.http.payload.*;

class ReflectionRoute implements AnyRoute {
  private final Supplier<Object> resource;
  private final Method method;

  ReflectionRoute(Supplier<Object> resource, Method method) {
    this.resource = resource;
    this.method = method;
  }

  @Override
  public Object body(Context context, String[] pathParameters) {
    try {
      Object target = resource.get();

      Object[] arguments = TypeConvert.convert(context, pathParameters, method.getGenericParameterTypes());
      Object response = invoke(target, method, arguments);
      Object payload = emptyIfNull(response);
      String contentType = findContentType(method);

      return new Payload(contentType, payload);
    } catch (RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new IllegalStateException("Unable to apply route", e);
    }
  }

  private static Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
    try {
      method.setAccessible(true);
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
