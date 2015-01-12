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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RouteSorter {
  private final List<RouteWithPattern> userRoutes;
  private final List<Route> catchAllRoutes;
  private final List<Route> staticRoutes;

  public RouteSorter() {
    this.userRoutes = new LinkedList<>();
    this.catchAllRoutes = new LinkedList<>();
    this.staticRoutes = new LinkedList<>();
  }

  public void addUserRoute(RouteWithPattern route) {
    userRoutes.add(route);
  }

  public void addCatchAllRoute(CatchAllRoute route) {
    catchAllRoutes.add(route);
  }

  public void addStaticRoute(Route route) {
    staticRoutes.add(route);
  }

  public Route[] getSortedRoutes() {
    List<Route> sorted = new ArrayList<>();

    userRoutes.stream().sorted((left, right) -> left.uriParser().compareTo(right.uriParser())).forEach(route -> sorted.add(route));
    staticRoutes.forEach(route -> sorted.add(route));
    catchAllRoutes.forEach(route -> sorted.add(route));

    return sorted.toArray(new Route[sorted.size()]);
  }
}
