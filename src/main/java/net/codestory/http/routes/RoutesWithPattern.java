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

public class RoutesWithPattern {
  private final Routes routes;
  private String currentUriPattern;

  public RoutesWithPattern(Routes routes, String currentUriPattern) {
    this.routes = routes;
    this.currentUriPattern = currentUriPattern;
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

  public RoutesWithPattern get(OneParamRouteWithContext route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(TwoParamsRouteWithContext route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(ThreeParamsRouteWithContext route) {
    routes.get(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern get(FourParamsRouteWithContext route) {
    routes.get(currentUriPattern, route);
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

  public RoutesWithPattern post(OneParamRouteWithContext route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(TwoParamsRouteWithContext route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(ThreeParamsRouteWithContext route) {
    routes.post(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern post(FourParamsRouteWithContext route) {
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

  public RoutesWithPattern put(OneParamRouteWithContext route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(TwoParamsRouteWithContext route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(ThreeParamsRouteWithContext route) {
    routes.put(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern put(FourParamsRouteWithContext route) {
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

  public RoutesWithPattern delete(OneParamRouteWithContext route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(TwoParamsRouteWithContext route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(ThreeParamsRouteWithContext route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern delete(FourParamsRouteWithContext route) {
    routes.delete(currentUriPattern, route);
    return this;
  }

  public RoutesWithPattern with(String uriPattern) {
    this.currentUriPattern = uriPattern;
    return this;
  }
}
