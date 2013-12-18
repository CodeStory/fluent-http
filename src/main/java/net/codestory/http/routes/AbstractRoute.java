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

import net.codestory.http.internal.*;
import net.codestory.http.payload.*;
import net.codestory.http.templating.*;

import org.simpleframework.http.*;

abstract class AbstractRoute implements Route {
  @Override
  public Payload apply(String uri, Context context) throws IOException {
    if (!matchUri(uri)) {
      if (!uri.endsWith("/") && matchUri(uri + "/")) {
        return Payload.seeOther(uri + "/");
      }
      return Payload.notFound();
    }

    if (!matchMethod(context)) {
      return Payload.methodNotAllowed();
    }

    String[] parameters = parseParameters(uri, context);

    Object body = body(context, parameters);
    if (body instanceof Model) {
      body = ModelAndView.of(uri, (Model) body);
    }

    return new Payload(body);
  }

  protected abstract Object body(Context context, String[] parameters);

  protected abstract boolean matchUri(String uri);

  protected abstract boolean matchMethod(Context context);

  protected abstract String[] parseParameters(String uri, Context context);
}
