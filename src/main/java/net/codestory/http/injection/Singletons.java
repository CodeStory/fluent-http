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
package net.codestory.http.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Singletons implements IocAdapter {
  private static final int MAX_DEPTH = 100;

  private final Map<Class<?>, Object> beansPerType;

  public Singletons(Object... beansToRegister) {
    this.beansPerType = new HashMap<>();

    register(Singletons.class, this);
    for (Object beanToRegister : beansToRegister) {
      Class<?> type = beanToRegister.getClass();

      // Hack to support Mockito Spies
      if (type.getName().contains("EnhancerByMockito")) {
        type = type.getSuperclass();
      }

      register(type, beanToRegister);
    }
  }

  public <T> Singletons register(Class<? extends T> type, T singleton) {
    beansPerType.put(type, singleton);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T> T get(Class<T> type) {
    // Fast path
    Object singleton = beansPerType.get(type);
    if (singleton != null) {
      return (T) singleton;
    }

    // Slow path
    return doGget(type, 0);
  }

  @SuppressWarnings("unchecked")
  private <T> T doGget(Class<T> type, int depth) {
    Object singleton = beansPerType.get(type);
    if (singleton != null) {
      return (T) singleton;
    }

    if (depth > MAX_DEPTH) {
      throw new IllegalStateException("Cycle in dependencies for " + type);
    }

    try {
      T instance = create(type, depth);
      beansPerType.put(type, instance);
      return instance;
    } catch (InvocationTargetException e) {
      throw new IllegalStateException("Unable to create instance of " + type + ". The constructor raised an exception", e.getCause());
    } catch (InstantiationException | IllegalAccessException | RuntimeException e) {
      throw new IllegalStateException("Unable to create instance of " + type, e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T create(Class<T> type, int depth) throws InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<T> constructor = getConstructor(type);
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      parameters[i] = doGget(parameterTypes[i], depth + 1);
    }

    return constructor.newInstance(parameters);
  }

  @SuppressWarnings("unchecked")
  private static <T> Constructor<T> getConstructor(Class<T> type) {
    try {
      return type.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      Constructor[] constructors = type.getConstructors();
      if (constructors.length != 1) {
        throw new IllegalStateException("Class " + type + " should have a single public constructor");
      }

      return constructors[0];
    }
  }
}
