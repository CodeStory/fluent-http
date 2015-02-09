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

import net.codestory.http.Context;
import net.codestory.http.payload.Payload;
import net.codestory.http.security.User;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Stream.of;

public class MethodAnnotationsFactory {
  private final Map<Class<? extends Annotation>, ApplyByPassAnnotation<? extends Annotation>> byPassAnnotations;
  private final Map<Class<? extends Annotation>, ApplyEnrichAnnotation<? extends Annotation>> enrichAnnotations;

  public MethodAnnotationsFactory() {
    this.byPassAnnotations = new LinkedHashMap<>();
    this.enrichAnnotations = new LinkedHashMap<>();
    registerStandardAnnotations();
  }

  public <T extends Annotation> void registerByPassAnnotation(Class<T> type, ApplyByPassAnnotation<T> apply) {
    byPassAnnotations.put(type, apply);
  }

  public <T extends Annotation> void registerEnrichAnnotation(Class<T> type, ApplyEnrichAnnotation<T> apply) {
    enrichAnnotations.put(type, apply);
  }

  public MethodAnnotations forMethod(Method method) {
    MethodAnnotations methodAnnotations = new MethodAnnotations();
    byPassAnnotations.forEach((annotationType, apply) -> addByPassOperationIdNecessary(annotationType, apply, method, methodAnnotations));
    enrichAnnotations.forEach((annotationType, apply) -> addEnrichOperationIdNecessary(annotationType, apply, method, methodAnnotations));
    return methodAnnotations;
  }

  @SuppressWarnings("unchecked")
  private <T extends Annotation> void addByPassOperationIdNecessary(Class<T> annotationType, ApplyByPassAnnotation<? extends Annotation> apply, Method method, MethodAnnotations methodAnnotations) {
    T annotation = method.getDeclaredAnnotation(annotationType);
    if (annotation != null) {
      methodAnnotations.addByPassOperation(context -> ((ApplyByPassAnnotation<T>) apply).apply(context, annotation));
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Annotation> void addEnrichOperationIdNecessary(Class<T> annotationType, ApplyEnrichAnnotation<? extends Annotation> apply, Method method, MethodAnnotations methodAnnotations) {
    T annotation = method.getDeclaredAnnotation(annotationType);
    if (annotation != null) {
      methodAnnotations.addEnrichOperation(context -> ((ApplyEnrichAnnotation<T>) apply).apply(context, annotation));
    }
  }

  private void registerStandardAnnotations() {
    registerByPassAnnotation(Roles.class, (context, roles) -> isAuthorized(roles, context.currentUser()) ? null : Payload.forbidden());
    registerEnrichAnnotation(AllowOrigin.class, (payload, origin) -> payload.withAllowOrigin(origin.value()));
    registerEnrichAnnotation(AllowMethods.class, (payload, methods) -> payload.withAllowMethods(methods.value()));
    registerEnrichAnnotation(AllowCredentials.class, (payload, credentials) -> payload.withAllowCredentials(credentials.value()));
    registerEnrichAnnotation(AllowHeaders.class, (payload, allowedHeaders) -> payload.withAllowHeaders(allowedHeaders.value()));
    registerEnrichAnnotation(ExposeHeaders.class, (payload, exposedHeaders) -> payload.withExposeHeaders(exposedHeaders.value()));
    registerEnrichAnnotation(MaxAge.class, (payload, maxAge) -> payload.withMaxAge(maxAge.value()));
  }

  private boolean isAuthorized(Roles roles, User user) {
    if (roles.allMatch()) {
      return of(roles.value()).allMatch(role -> user.isInRole(role));
    } else {
      return of(roles.value()).anyMatch(role -> user.isInRole(role));
    }
  }

  @FunctionalInterface
  static interface ApplyByPassAnnotation<T extends Annotation> {
    Payload apply(Context context, T annotation);
  }

  @FunctionalInterface
  static interface ApplyEnrichAnnotation<T extends Annotation> {
    Payload apply(Payload payload, T annotation);
  }
}
