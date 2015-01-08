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

import java.util.LinkedList;

import static java.util.stream.Stream.of;

public class RouteSorter {
  private final LinkedList<RouteWrapper> userRoutes;
  private final LinkedList<Route> catchAllRoutes;
  private final LinkedList<Route> staticRoutes;

  public RouteSorter() {
    this.userRoutes = new LinkedList<>();
    this.catchAllRoutes = new LinkedList<>();
    this.staticRoutes = new LinkedList<>();
  }

  public void addUserRoute(RouteWrapper route) {
    userRoutes.add(route);
  }

  public void addCatchAllRoute(CatchAllRoute route) {
    catchAllRoutes.add(route);
  }

  public void addStaticRoute(Route route) {
    staticRoutes.add(route);
  }

  public Route[] getSortedRoutes() {
    // TODO: don't sort the original list
    userRoutes.sort((left, right) -> left.uriParser().compareTo(right.uriParser()));

    return of(userRoutes, staticRoutes, catchAllRoutes)
      .flatMap(routes -> routes.stream())
      .toArray(Route[]::new);
  }
}
