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

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

public class RoutePrecedenceTest extends AbstractProdWebServerTest {
  @Test
  public void override_static_file_with_route() {
    configure(routes -> routes
        .get("/", "PRIORITY")
    );

    get("/").should().contain("PRIORITY");
  }

  @Test
  public void routes_can_be_overriden() {
    configure(routes -> routes
        .get("/", "FIRST")
        .get("/", "SECOND")
    );

    get("/").should().contain("SECOND");
  }

  @Test
  public void multiple_routes_same_uri() {
    configure(routes -> routes
        .url("/").
          get(() -> "Index GET").
          post(() -> "Index POST")
        .url("/action").
          get(() -> "Action GET").
          post(() -> "Action POST")
    );

    get("/").should().contain("Index GET");
    post("/").should().contain("Index POST");
    get("/action").should().contain("Action GET");
    post("/action").should().contain("Action POST");
  }

  @Test
  public void fixed_route_comes_first() {
    configure(routes -> routes
        .get("/foo", "One")
        .get("/:param", (context, param) -> "Two")
    );

    get("/foo").should().contain("One");
    get("/bar").should().contain("Two");
  }

  @Test
  public void fixed_routes_comes_first_ignore_insertion_order() {
    configure(routes -> routes
        .get("/:param", (context, param) -> "One")
        .get("/foo", "Two")
    );

    get("/foo").should().contain("Two");
    get("/bar").should().contain("One");
  }

  @Test
  public void catch_all_is_always_last() {
    configure(routes -> routes
        .get("/bar/:endparam", (context, param) -> "One")
        .get("/foo", "Two")
        .any((context) -> "last")
        .anyGet((context) -> "antepenultimate")
    );

    get("/foo").should().contain("Two");
    get("/bar/baz").should().contain("One");
    get("/catch/all/get").should().contain("antepenultimate");
    post("/catch/all").should().contain("last");
    put("/catch/all").should().contain("last");
  }
}
