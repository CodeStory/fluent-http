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
package net.codestory.http.routes;

import net.codestory.http.extensions.Extensions;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;

public class RoutesForwarder implements Routes {
  private final Routes[] routes;

  public RoutesForwarder(Routes... routes) {
    this.routes = routes;
  }

  protected Routes[] delegates() {
    return routes;
  }

  @Override
  public Routes setExtensions(Extensions extensions) {
    for (Routes routes : delegates()) {
      routes.setExtensions(extensions);
    }
    return this;
  }

  @Override
  public Routes setIocAdapter(IocAdapter iocAdapter) {
    for (Routes routes : delegates()) {
      routes.setIocAdapter(iocAdapter);
    }
    return this;
  }

  @Override
  public Routes filter(Class<? extends Filter> filterClass) {
    for (Routes routes : delegates()) {
      routes.filter(filterClass);
    }
    return this;
  }

  @Override
  public Routes filter(Filter filter) {
    for (Routes routes : delegates()) {
      routes.filter(filter);
    }
    return this;
  }

  @Override
  public Routes add(Class<?> resource) {
    for (Routes routes : delegates()) {
      routes.add(resource);
    }
    return this;
  }

  @Override
  public Routes add(String urlPrefix, Class<?> resource) {
    for (Routes routes : delegates()) {
      routes.add(urlPrefix, resource);
    }
    return this;
  }

  @Override
  public Routes add(Object resource) {
    for (Routes routes : delegates()) {
      routes.add(resource);
    }
    return this;
  }

  @Override
  public Routes add(String urlPrefix, Object resource) {
    for (Routes routes : delegates()) {
      routes.add(urlPrefix, resource);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, Object payload) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, payload);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes get(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.get(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, Object payload) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, payload);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes head(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.head(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes post(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.post(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes put(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.put(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, Object payload) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, payload);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes options(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.options(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, OneParamRoute route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, TwoParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, ThreeParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes delete(String uriPattern, FourParamsRoute route) {
    for (Routes routes : delegates()) {
      routes.delete(uriPattern, route);
    }
    return this;
  }

  @Override
  public Routes catchAll(Object payload) {
    for (Routes routes : delegates()) {
      routes.catchAll(payload);
    }
    return this;
  }

  @Override
  public Routes catchAll(NoParamRoute route) {
    for (Routes routes : delegates()) {
      routes.catchAll(route);
    }
    return this;
  }

  @Override
  public Routes catchAll(NoParamRouteWithContext route) {
    for (Routes routes : delegates()) {
      routes.catchAll(route);
    }
    return this;
  }

  @Override
  public RoutesWithPattern with(String uriPattern) {
    throw new UnsupportedOperationException();
  }
}
