/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import static net.codestory.http.constants.Methods.GET;
import static net.codestory.http.constants.Methods.HEAD;

import java.util.Arrays;
import java.util.Objects;

import net.codestory.http.Context;

class RouteWithPattern implements Route {
  private final String method;
  private final UriParser uriParser;
  private final AnyRoute route;

  RouteWithPattern(String method, String uriPattern, AnyRoute route) {
    this.method = method;
    this.uriParser = new UriParser(uriPattern);
    this.route = route;
  }

  public UriParser uriParser() {
    return uriParser;
  }

  @Override
  public boolean matchUri(String uri) {
    return uriParser.matches(uri);
  }

  @Override
  public boolean matchMethod(String method) {
    return this.method.equalsIgnoreCase(method) || (HEAD.equalsIgnoreCase(method) && this.method.equalsIgnoreCase(GET));
  }

  @Override
  public Object body(Context context) throws Exception {
    String[] parameters = uriParser.params(context.uri(), context.request().query());
    return route.body(context, parameters);
  }

  @Override
  public String toString() {
    return "RouteWithPattern: (" + method + ") " + uriParser.uriPattern();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{method, uriParser});
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RouteWithPattern) {
      RouteWithPattern other = (RouteWithPattern) obj;

      return Objects.equals(method, other.method) && uriParser.equals(other.uriParser);
    }

    return false;
  }
}
