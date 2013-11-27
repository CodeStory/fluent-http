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

import net.codestory.http.payload.*;
import net.codestory.http.templating.*;

import org.simpleframework.http.*;

abstract class AbstractRoute implements Route {
  @Override
  public Match apply(String uri, Request request, Response response) throws IOException {
    if (!matchUri(uri)) {
      if (!uri.endsWith("/") && matchUri(uri + "/")) {
        return TRY_WITH_LEADING_SLASH;
      }
      return WRONG_URL;
    }

    if (!matchMethod(request)) {
      return WRONG_METHOD;
    }

    String[] parameters = parseParameters(uri, request);

    Object body = body(request, response, parameters);
    if (body instanceof Model) {
      body = ModelAndView.of(uri, (Model) body);
    }

    Payload payload = new Payload(body);
    payload.writeTo(response);

    return OK;
  }

  protected abstract Object body(Request request, Response response, String[] parameters);

  protected abstract boolean matchUri(String uri);

  protected abstract boolean matchMethod(Request request);

  protected abstract String[] parseParameters(String uri, Request request);
}
