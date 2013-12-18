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

import net.codestory.http.internal.*;

abstract class AbstractRouteWrapper extends AbstractRoute {
  private final String method;
  private final UriParser uriParser;

  protected AbstractRouteWrapper(String method, String uriPattern) {
    this.method = method;
    this.uriParser = new UriParser(uriPattern);
  }

  protected boolean matchUri(String uri) {
    return uriParser.matches(uri);
  }

  protected boolean matchMethod(Context context) {
    return method.equalsIgnoreCase(context.method());
  }

  protected String[] parseParameters(String uri, Context context) {
    return uriParser.params(uri, context.request().getQuery());
  }
}
