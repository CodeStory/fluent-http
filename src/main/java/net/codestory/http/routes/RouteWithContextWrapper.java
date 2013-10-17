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

import static net.codestory.http.routes.Match.*;

import java.io.*;

import net.codestory.http.*;
import net.codestory.http.internal.*;

import org.simpleframework.http.*;

class RouteWithContextWrapper implements Route {
  private final String method;
  private final UriParser uriParser;
  private final AnyRouteWithContext route;

  RouteWithContextWrapper(String method, String uriPattern, AnyRouteWithContext route) {
    this.method = method;
    this.uriParser = new UriParser(uriPattern);
    this.route = route;
  }

  @Override
  public Match apply(String uri, Request request, Response response) throws IOException {
    if (!uriParser.matches(uri)) {
      return WRONG_URL;
    }

    if (!method.equalsIgnoreCase(request.getMethod())) {
      return WRONG_METHOD;
    }

    String[] parameters = uriParser.params(uri, request.getQuery());
    Object body = route.body(new Context(request), parameters);

    Payload payload = Payload.wrap(body);
    payload.writeTo(response);

    return OK;
  }
}
