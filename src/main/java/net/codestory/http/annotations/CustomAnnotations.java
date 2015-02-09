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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Stream.of;

public class CustomAnnotations {
  private final Method method;
  private final List<Function<Context, Payload>> byPassOperations;
  private final List<Function<Payload, Payload>> enrichOperations;

  public CustomAnnotations(Method method) {
    this.method = method;
    this.byPassOperations = new ArrayList<>();
    this.enrichOperations = new ArrayList<>();

    registerStandardAnnotations();
  }

  public Payload byPass(Context context) {
    for (Function<Context, Payload> operation : byPassOperations) {
      Payload payload = operation.apply(context);
      if (payload != null) {
        return payload;
      }
    }

    return null;
  }

  public Payload enrich(Payload payload) {
    for (Function<Payload, Payload> operation : enrichOperations) {
      payload = operation.apply(payload);
    }

    return payload;
  }

  private void registerStandardAnnotations() {
    byPassOperations.add(context -> isAuthorized(context.currentUser()) ? null : Payload.forbidden());

    AllowOrigin origin = method.getDeclaredAnnotation(AllowOrigin.class);
    if (origin != null) {
      enrichOperations.add(payload -> payload.withAllowOrigin(origin.value()));
    }

    AllowMethods methods = method.getDeclaredAnnotation(AllowMethods.class);
    if (methods != null) {
      enrichOperations.add(payload -> payload.withAllowMethods(methods.value()));
    }

    AllowCredentials credentials = method.getDeclaredAnnotation(AllowCredentials.class);
    if (credentials != null) {
      enrichOperations.add(payload -> payload.withAllowCredentials(credentials.value()));
    }

    AllowHeaders allowedHeaders = method.getDeclaredAnnotation(AllowHeaders.class);
    if (allowedHeaders != null) {
      enrichOperations.add(payload -> payload.withAllowHeaders(allowedHeaders.value()));
    }

    ExposeHeaders exposedHeaders = method.getDeclaredAnnotation(ExposeHeaders.class);
    if (exposedHeaders != null) {
      enrichOperations.add(payload -> payload.withExposeHeaders(exposedHeaders.value()));
    }

    MaxAge maxAge = method.getDeclaredAnnotation(MaxAge.class);
    if (maxAge != null) {
      enrichOperations.add(payload -> payload.withMaxAge(maxAge.value()));
    }
  }

  private boolean isAuthorized(User user) {
    Roles roles = method.getDeclaredAnnotation(Roles.class);
    if (roles == null) {
      return true;
    }

    if (roles.allMatch()) {
      return of(roles.value()).allMatch(role -> user.isInRole(role));
    } else {
      return of(roles.value()).anyMatch(role -> user.isInRole(role));
    }
  }
}
