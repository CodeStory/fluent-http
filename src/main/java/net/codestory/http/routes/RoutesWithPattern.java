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

import java.io.*;

public class RoutesWithPattern {
  private final Routes routes;
  private final String currentUriPattern;

  public RoutesWithPattern(Routes routes, String currentUriPattern) {
    this.routes = routes;
    this.currentUriPattern = currentUriPattern;
  }

  public RoutesWithPattern url(String uriPattern) {
    return new RoutesWithPattern(routes, uriPattern);
  }

  public RoutesWithPattern get(Object payload) {
    routes.get(currentUriPattern, payload);
    return this;
  }

  public RoutesWithPattern get(NoParamRoute route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(NoParamRouteWithContext route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(OneParamRoute route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(TwoParamsRoute route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(ThreeParamsRoute route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(FourParamsRoute route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(Object payload) {
    routes.options(currentUriPattern, payload);
    return this;
  }

  public RoutesWithPattern options(NoParamRoute route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(NoParamRouteWithContext route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(OneParamRoute route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(TwoParamsRoute route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(ThreeParamsRoute route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern options(FourParamsRoute route) {
    routes.options(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(Object payload) {
    routes.head(currentUriPattern, payload);
    return this;
  }

  public RoutesWithPattern head(NoParamRoute route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(NoParamRouteWithContext route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(OneParamRoute route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(TwoParamsRoute route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(ThreeParamsRoute route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern head(FourParamsRoute route) {
    routes.head(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(NoParamRoute route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(NoParamRouteWithContext route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(OneParamRoute route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(TwoParamsRoute route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(ThreeParamsRoute route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(FourParamsRoute route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(NoParamRoute route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(NoParamRouteWithContext route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(OneParamRoute route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(TwoParamsRoute route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(ThreeParamsRoute route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(FourParamsRoute route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(NoParamRoute route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(NoParamRouteWithContext route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(OneParamRoute route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(TwoParamsRoute route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(ThreeParamsRoute route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(FourParamsRoute route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern bind(String uriRoot, File path) {
    routes.bind(currentUriPattern + uriRoot, path);
    return this;
  }
}
