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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RouteSorter {
  private final Map<RouteWithPattern, RouteWithPattern> userRoutes;
  private final Map<CatchAllRoute, CatchAllRoute> catchAllRoutes;
  private final List<Route> staticRoutes;

  public RouteSorter() {
    this.userRoutes = new HashMap<>();
    this.catchAllRoutes = new HashMap<>();
    this.staticRoutes = new LinkedList<>();
  }

  public void addUserRoute(RouteWithPattern route) {
    userRoutes.put(route, route);
  }

  public void addCatchAllRoute(CatchAllRoute route) {
    catchAllRoutes.put(route, route);
  }

  public void addStaticRoute(Route route) {
    staticRoutes.add(route);
  }

  public Route[] getSortedRoutes() {
    List<Route> sorted = new ArrayList<>();

    userRoutes.values().stream().sorted((left, right) -> left.uriParser().compareTo(right.uriParser())).forEach(sorted::add);
    staticRoutes.forEach(sorted::add);
    catchAllRoutes.values().stream().sorted((left, right) -> right.getMethod().compareTo(left.getMethod())).forEach(sorted::add);

    return sorted.toArray(new Route[sorted.size()]);
  }
}
