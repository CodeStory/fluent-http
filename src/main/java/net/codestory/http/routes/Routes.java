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

import net.codestory.http.filters.*;
import net.codestory.http.injection.*;

public interface Routes {
  void filter(Class<? extends Filter> resource);

  void filter(Filter filter);

  void setIocAdapter(IocAdapter iocAdapter);

  void add(Class<?> resource);

  void add(String urlPrefix, Class<?> resource);

  void add(Object resource);

  void add(String urlPrefix, Object resource);

  void get(String uriPattern, Object payload);

  void get(String uriPattern, NoParamRoute route);

  void get(String uriPattern, OneParamRoute route);

  void get(String uriPattern, TwoParamsRoute route);

  void get(String uriPattern, ThreeParamsRoute route);

  void get(String uriPattern, FourParamsRoute route);

  void get(String uriPattern, NoParamRouteWithContext route);

  void get(String uriPattern, OneParamRouteWithContext route);

  void get(String uriPattern, TwoParamsRouteWithContext route);

  void get(String uriPattern, ThreeParamsRouteWithContext route);

  void get(String uriPattern, FourParamsRouteWithContext route);

  void post(String uriPattern, NoParamRoute route);

  void post(String uriPattern, OneParamRoute route);

  void post(String uriPattern, TwoParamsRoute route);

  void post(String uriPattern, ThreeParamsRoute route);

  void post(String uriPattern, FourParamsRoute route);

  void post(String uriPattern, NoParamRouteWithContext route);

  void post(String uriPattern, OneParamRouteWithContext route);

  void post(String uriPattern, TwoParamsRouteWithContext route);

  void post(String uriPattern, ThreeParamsRouteWithContext route);

  void post(String uriPattern, FourParamsRouteWithContext route);

  void put(String uriPattern, NoParamRoute route);

  void put(String uriPattern, OneParamRoute route);

  void put(String uriPattern, TwoParamsRoute route);

  void put(String uriPattern, ThreeParamsRoute route);

  void put(String uriPattern, FourParamsRoute route);

  void put(String uriPattern, NoParamRouteWithContext route);

  void put(String uriPattern, OneParamRouteWithContext route);

  void put(String uriPattern, TwoParamsRouteWithContext route);

  void put(String uriPattern, ThreeParamsRouteWithContext route);

  void put(String uriPattern, FourParamsRouteWithContext route);
}
