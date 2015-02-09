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

import net.codestory.http.payload.Payload;
import net.codestory.http.security.User;

import java.lang.reflect.Method;

import static java.util.stream.Stream.of;

public class MethodAnnotationsFactory {
  public MethodAnnotations forMethod(Method method) {
    MethodAnnotations methodAnnotations = new MethodAnnotations();
    registerStandardAnnotations(method, methodAnnotations);
    return methodAnnotations;
  }

  private void registerStandardAnnotations(Method method, MethodAnnotations methodAnnotations) {
    Roles roles = method.getDeclaredAnnotation(Roles.class);
    if (roles != null) {
      methodAnnotations.addByPassOperation(context -> isAuthorized(roles, context.currentUser()) ? null : Payload.forbidden());
    }

    AllowOrigin origin = method.getDeclaredAnnotation(AllowOrigin.class);
    if (origin != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withAllowOrigin(origin.value()));
    }

    AllowMethods methods = method.getDeclaredAnnotation(AllowMethods.class);
    if (methods != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withAllowMethods(methods.value()));
    }

    AllowCredentials credentials = method.getDeclaredAnnotation(AllowCredentials.class);
    if (credentials != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withAllowCredentials(credentials.value()));
    }

    AllowHeaders allowedHeaders = method.getDeclaredAnnotation(AllowHeaders.class);
    if (allowedHeaders != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withAllowHeaders(allowedHeaders.value()));
    }

    ExposeHeaders exposedHeaders = method.getDeclaredAnnotation(ExposeHeaders.class);
    if (exposedHeaders != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withExposeHeaders(exposedHeaders.value()));
    }

    MaxAge maxAge = method.getDeclaredAnnotation(MaxAge.class);
    if (maxAge != null) {
      methodAnnotations.addEnrichOperation(payload -> payload.withMaxAge(maxAge.value()));
    }
  }

  private boolean isAuthorized(Roles roles, User user) {
    if (roles.allMatch()) {
      return of(roles.value()).allMatch(role -> user.isInRole(role));
    } else {
      return of(roles.value()).anyMatch(role -> user.isInRole(role));
    }
  }
}
