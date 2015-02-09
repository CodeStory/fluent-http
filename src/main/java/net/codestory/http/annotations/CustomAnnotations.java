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

  private void registerStandardAnnotations() {
    byPassOperations.add(context -> isAuthorized(context.currentUser()) ? null : Payload.forbidden());
    enrichOperations.add(payload -> {
      AllowOrigin origin = method.getDeclaredAnnotation(AllowOrigin.class);
      return (origin != null) ? payload.withAllowOrigin(origin.value()) : payload;
    });
    enrichOperations.add(payload -> {
      AllowMethods methods = method.getDeclaredAnnotation(AllowMethods.class);
      return (methods != null) ? payload.withAllowMethods(methods.value()) : payload;
    });
    enrichOperations.add(payload -> {
      AllowCredentials credentials = method.getDeclaredAnnotation(AllowCredentials.class);
      return (credentials != null) ? payload.withAllowCredentials(credentials.value()) : payload;
    });
    enrichOperations.add(payload -> {
      AllowHeaders allowedHeaders = method.getDeclaredAnnotation(AllowHeaders.class);
      return (allowedHeaders != null) ? payload.withAllowHeaders(allowedHeaders.value()) : payload;
    });
    enrichOperations.add(payload -> {
      ExposeHeaders exposedHeaders = method.getDeclaredAnnotation(ExposeHeaders.class);
      return (exposedHeaders != null) ? payload.withExposeHeaders(exposedHeaders.value()) : payload;
    });
    enrichOperations.add(payload -> {
      MaxAge maxAge = method.getDeclaredAnnotation(MaxAge.class);
      return (maxAge != null) ? payload.withMaxAge(maxAge.value()) : payload;
    });
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
