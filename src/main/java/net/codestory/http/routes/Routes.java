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

import java.io.*;

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.injection.*;

public interface Routes extends Serializable {
  Routes setIocAdapter(IocAdapter iocAdapter);

  Routes include(Class<? extends Configuration> configurationClass);

  Routes include(Configuration configuration);

  Routes filter(Class<? extends Filter> filterClass);

  Routes filter(Filter filter);

  Routes add(Class<?> resource);

  Routes add(String urlPrefix, Class<?> resource);

  Routes add(Object resource);

  Routes add(String urlPrefix, Object resource);

  Routes get(String uriPattern, Object payload);

  Routes get(String uriPattern, NoParamRoute route);

  Routes get(String uriPattern, NoParamRouteWithContext route);

  Routes get(String uriPattern, OneParamRoute route);

  Routes get(String uriPattern, TwoParamsRoute route);

  Routes get(String uriPattern, ThreeParamsRoute route);

  Routes get(String uriPattern, FourParamsRoute route);

  Routes head(String uriPattern, Object payload);

  Routes head(String uriPattern, NoParamRoute route);

  Routes head(String uriPattern, NoParamRouteWithContext route);

  Routes head(String uriPattern, OneParamRoute route);

  Routes head(String uriPattern, TwoParamsRoute route);

  Routes head(String uriPattern, ThreeParamsRoute route);

  Routes head(String uriPattern, FourParamsRoute route);

  Routes post(String uriPattern, NoParamRoute route);

  Routes post(String uriPattern, NoParamRouteWithContext route);

  Routes post(String uriPattern, OneParamRoute route);

  Routes post(String uriPattern, TwoParamsRoute route);

  Routes post(String uriPattern, ThreeParamsRoute route);

  Routes post(String uriPattern, FourParamsRoute route);

  Routes put(String uriPattern, NoParamRoute route);

  Routes put(String uriPattern, NoParamRouteWithContext route);

  Routes put(String uriPattern, OneParamRoute route);

  Routes put(String uriPattern, TwoParamsRoute route);

  Routes put(String uriPattern, ThreeParamsRoute route);

  Routes put(String uriPattern, FourParamsRoute route);

  Routes options(String uriPattern, Object payload);

  Routes options(String uriPattern, NoParamRoute route);

  Routes options(String uriPattern, NoParamRouteWithContext route);

  Routes options(String uriPattern, OneParamRoute route);

  Routes options(String uriPattern, TwoParamsRoute route);

  Routes options(String uriPattern, ThreeParamsRoute route);

  Routes options(String uriPattern, FourParamsRoute route);

  Routes delete(String uriPattern, NoParamRoute route);

  Routes delete(String uriPattern, NoParamRouteWithContext route);

  Routes delete(String uriPattern, OneParamRoute route);

  Routes delete(String uriPattern, TwoParamsRoute route);

  Routes delete(String uriPattern, ThreeParamsRoute route);

  Routes delete(String uriPattern, FourParamsRoute route);

  Routes catchAll(Object payload);

  Routes catchAll(NoParamRoute route);

  Routes catchAll(NoParamRouteWithContext route);

  RoutesWithPattern with(String uriPattern);
}
