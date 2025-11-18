/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import net.codestory.http.Context;
import net.codestory.http.annotations.MethodAnnotations;
import net.codestory.http.annotations.Produces;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.payload.Payload;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Supplier;

class ReflectionRoute implements AnyRoute {
  private final Supplier<Object> resource;
  private final Method method;
  private final MethodAnnotations annotations;

  ReflectionRoute(Supplier<Object> resource, Method method, MethodAnnotations annotations) {
    this.resource = resource;
    this.method = method;
    this.annotations = annotations;
  }

  @Override
  public Object body(Context context, String[] pathParameters) {
    return annotations.apply(context, ctx -> {
      try {
        Object target = resource.get();

        Object[] arguments = convert(ctx, pathParameters, method.getGenericParameterTypes());
        Object response = invoke(target, method, arguments);
        Object body = emptyIfNull(response);
        String contentType = findContentType(method);

        return new Payload(contentType, body);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        }
        throw new IllegalStateException("Unable to apply route", e.getCause());
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new IllegalStateException("Unable to apply route", e);
      }
    });
  }

  public Method javaMethod() {return method;}

  static Object[] convert(Context context, String[] pathParameters, Type... types) throws IOException {
    Object[] converted = new Object[types.length];

    // String parameters
    for (int i = 0; i < pathParameters.length; i++) {
      converted[i] = TypeConvert.convertValue(pathParameters[i], types[i]);
    }

    // Other parameters
    for (int i = pathParameters.length; i < converted.length; i++) {
      converted[i] = context.extract(types[i]);
    }

    return converted;
  }

  private static Object invoke(Object target, Method method, Object[] arguments) throws InvocationTargetException, IllegalAccessException {
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }
      return method.invoke(target, arguments);
  }

  private static Object emptyIfNull(Object payload) {
    return (payload == null) ? "" : payload;
  }

  private static String findContentType(Method method) {
    Produces annotation = method.getAnnotation(Produces.class);
    return (annotation == null) ? null : annotation.value();
  }
}
