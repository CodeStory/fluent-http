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

import net.codestory.http.annotations.ApplyByPassAnnotation;
import net.codestory.http.annotations.ApplyEnrichAnnotation;
import net.codestory.http.extensions.Extensions;
import net.codestory.http.filters.Filter;
import net.codestory.http.injection.IocAdapter;
import net.codestory.http.websockets.WebSocketListenerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;

public interface Routes extends Serializable {
  Routes setExtensions(Extensions extensions);

  Routes setIocAdapter(IocAdapter iocAdapter);

  Routes setWebSocketListenerFactory(WebSocketListenerFactory factory);

  Routes filter(Class<? extends Filter> filterClass);

  Routes filter(Filter filter);

  Routes add(Class<?> resource);

  Routes add(String urlPrefix, Class<?> resource);

  Routes add(Object resource);

  Routes add(String urlPrefix, Object resource);

  Routes any(NoParamRouteWithContext route);

  Routes anyGet(NoParamRouteWithContext route);

  Routes get(String uriPattern, Object payload);

  Routes get(String uriPattern, NoParamRoute route);

  Routes get(String uriPattern, NoParamRouteWithContext route);

  Routes get(String uriPattern, OneParamRoute route);

  Routes get(String uriPattern, TwoParamsRoute route);

  Routes get(String uriPattern, ThreeParamsRoute route);

  Routes get(String uriPattern, FourParamsRoute route);

  Routes anyHead(NoParamRouteWithContext route);

  Routes head(String uriPattern, Object payload);

  Routes head(String uriPattern, NoParamRoute route);

  Routes head(String uriPattern, NoParamRouteWithContext route);

  Routes head(String uriPattern, OneParamRoute route);

  Routes head(String uriPattern, TwoParamsRoute route);

  Routes head(String uriPattern, ThreeParamsRoute route);

  Routes head(String uriPattern, FourParamsRoute route);

  Routes anyPost(NoParamRouteWithContext route);

  Routes post(String uriPattern, NoParamRoute route);

  Routes post(String uriPattern, NoParamRouteWithContext route);

  Routes post(String uriPattern, OneParamRoute route);

  Routes post(String uriPattern, TwoParamsRoute route);

  Routes post(String uriPattern, ThreeParamsRoute route);

  Routes post(String uriPattern, FourParamsRoute route);

  Routes anyPut(NoParamRouteWithContext route);

  Routes put(String uriPattern, NoParamRoute route);

  Routes put(String uriPattern, NoParamRouteWithContext route);

  Routes put(String uriPattern, OneParamRoute route);

  Routes put(String uriPattern, TwoParamsRoute route);

  Routes put(String uriPattern, ThreeParamsRoute route);

  Routes put(String uriPattern, FourParamsRoute route);

  Routes anyOptions(NoParamRouteWithContext route);

  Routes options(String uriPattern, Object payload);

  Routes options(String uriPattern, NoParamRoute route);

  Routes options(String uriPattern, NoParamRouteWithContext route);

  Routes options(String uriPattern, OneParamRoute route);

  Routes options(String uriPattern, TwoParamsRoute route);

  Routes options(String uriPattern, ThreeParamsRoute route);

  Routes options(String uriPattern, FourParamsRoute route);

  Routes anyDelete(NoParamRouteWithContext route);

  Routes delete(String uriPattern, NoParamRoute route);

  Routes delete(String uriPattern, NoParamRouteWithContext route);

  Routes delete(String uriPattern, OneParamRoute route);

  Routes delete(String uriPattern, TwoParamsRoute route);

  Routes delete(String uriPattern, ThreeParamsRoute route);

  Routes delete(String uriPattern, FourParamsRoute route);

  Routes autoDiscover(String packageToScan);

  <T extends Annotation> Routes registerByPassAnnotation(Class<T> annotationType, ApplyByPassAnnotation<T> apply);

  <T extends Annotation> Routes registerEnrichAnnotation(Class<T> annotationType, ApplyEnrichAnnotation<T> apply);

  RoutesWithPattern url(String uriPattern);
}
