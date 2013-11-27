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

import net.codestory.http.internal.*;
import net.codestory.http.payload.*;
import net.codestory.http.templating.*;

import org.simpleframework.http.*;

abstract class AbstractRouteWrapper implements Route {
  private final String method;
  private final UriParser uriParser;

  protected AbstractRouteWrapper(String method, String uriPattern) {
    this.method = method;
    this.uriParser = new UriParser(uriPattern);
  }

  @Override
  public Match apply(String uri, Request request, Response response) throws IOException {
    if (!uriParser.matches(uri)) {
      if (!uri.endsWith("/") && uriParser.matches(uri + "/")) {
        return TRY_WITH_LEADING_SLASH;
      }
      return WRONG_URL;
    }

    if (!method.equalsIgnoreCase(request.getMethod())) {
      return WRONG_METHOD;
    }

    String[] parameters = uriParser.params(uri, request.getQuery());
    Object body = body(request, response, parameters);

    if (body instanceof Model) {
      body = ModelAndView.of(uri, (Model) body);
    }

    Payload payload = new Payload(body);
    payload.writeTo(response);

    return OK;
  }

  protected abstract Object body(Request request, Response response, String[] parameters);
}
