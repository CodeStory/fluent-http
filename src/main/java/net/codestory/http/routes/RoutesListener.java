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

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;

public class RoutesListener implements Routes {
  @Override
  public Routes setIocAdapter(IocAdapter iocAdapter) {
    return this;
  }

  @Override
  public Routes include(Class<? extends Configuration> configurationClass) {
    return this;
  }

  @Override
  public Routes include(Configuration configuration) {
    return this;
  }

  @Override
  public Routes filter(Class<? extends Filter> filterClass) {
    return this;
  }

  @Override
  public Routes filter(Filter filter) {
    return this;
  }

  @Override
  public Routes add(Class<?> resource) {
    return this;
  }

  @Override
  public Routes add(String urlPrefix, Class<?> resource) {
    return this;
  }

  @Override
  public Routes add(Object resource) {
    return this;
  }

  @Override
  public Routes add(String urlPrefix, Object resource) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, Object payload) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes get(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, Object payload) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes head(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes post(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes put(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, Object payload) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes options(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, NoParamRoute route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, OneParamRoute route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, TwoParamsRoute route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, ThreeParamsRoute route) {
    return this;
  }

  @Override
  public Routes delete(String uriPattern, FourParamsRoute route) {
    return this;
  }

  @Override
  public Routes catchAll(Object payload) {
    return this;
  }

  @Override
  public Routes catchAll(NoParamRoute route) {
    return this;
  }

  @Override
  public Routes catchAll(NoParamRouteWithContext route) {
    return this;
  }

  @Override
  public RoutesWithPattern with(String uriPattern) {
    throw new UnsupportedOperationException();
  }
}
