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
package net.codestory.http.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodAnnotationsFactory {
  private final Map<Class<? extends Annotation>, ApplyByPassAnnotation<? extends Annotation>> byPassAnnotations;
  private final Map<Class<? extends Annotation>, ApplyAfterAnnotation<? extends Annotation>> afterAnnotations;

  public MethodAnnotationsFactory() {
    this.byPassAnnotations = new LinkedHashMap<>();
    this.afterAnnotations = new LinkedHashMap<>();
  }

  public <T extends Annotation> void registerByPassAnnotation(Class<T> type, ApplyByPassAnnotation<T> apply) {
    byPassAnnotations.put(type, apply);
  }

  public <T extends Annotation> void registerAfterAnnotation(Class<T> type, ApplyAfterAnnotation<T> apply) {
    afterAnnotations.put(type, apply);
  }

  public MethodAnnotations forMethod(Method method) {
    MethodAnnotations methodAnnotations = new MethodAnnotations();
    byPassAnnotations.forEach((annotationType, apply) -> addByPassOperationIfNecessary(annotationType, apply, method, methodAnnotations));
    afterAnnotations.forEach((annotationType, apply) -> addAfterOperationIfNecessary(annotationType, apply, method, methodAnnotations));
    return methodAnnotations;
  }

  @SuppressWarnings("unchecked")
  private <T extends Annotation> void addByPassOperationIfNecessary(Class<T> annotationType, ApplyByPassAnnotation<? extends Annotation> apply, Method method, MethodAnnotations methodAnnotations) {
    T annotation = findAnnotationOnMethodOrClass(annotationType, method);
    if (annotation != null) {
      methodAnnotations.addByPassOperation(context -> ((ApplyByPassAnnotation<T>) apply).apply(context, annotation));
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Annotation> void addAfterOperationIfNecessary(Class<T> annotationType, ApplyAfterAnnotation<? extends Annotation> apply, Method method, MethodAnnotations methodAnnotations) {
    T annotation = findAnnotationOnMethodOrClass(annotationType, method);
    if (annotation != null) {
      methodAnnotations.addAfterOperation(context -> ((ApplyAfterAnnotation<T>) apply).apply(context, annotation));
    }
  }

  private <T extends Annotation> T findAnnotationOnMethodOrClass(Class<T> annotationType, Method method) {
    T annotation = method.getDeclaredAnnotation(annotationType);
    if (annotation != null) {
      return annotation;
    }

    annotation = method.getDeclaringClass().getDeclaredAnnotation(annotationType);
    if (annotation != null) {
      return annotation;
    }

    return null;
  }
}
