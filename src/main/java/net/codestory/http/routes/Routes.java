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

public interface Routes {
  void filter(Filter filter);

  void add(Object resource);

  void add(String urlPrefix, Object resource);

  void get(String uriPattern, Payload payload);

  void get(String uriPattern, NoParamGetRoute route);

  void get(String uriPattern, OneParamGetRoute route);

  void get(String uriPattern, TwoParamsGetRoute route);

  void get(String uriPattern, ThreeParamsGetRoute route);

  void get(String uriPattern, FourParamsGetRoute route);

  void post(String uriPattern, NoParamPostRoute route);

  void post(String uriPattern, OneParamPostRoute route);

  void post(String uriPattern, TwoParamsPostRoute route);

  void post(String uriPattern, ThreeParamsPostRoute route);

  void post(String uriPattern, FourParamsPostRoute route);
}
