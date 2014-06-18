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
package net.codestory.http;

import net.codestory.http.routes.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class ConfigurationTest extends AbstractProdWebServerTest {
  @Test
  public void priority_to_route_over_file() {
    server.configure(routes -> routes.get("/", "PRIORITY"));

    get("/").produces("PRIORITY");
  }

  @Test
  public void first_route_serves_first() {
    server.configure(routes -> routes.
      get("/", "FIRST").
      get("/", "SECOND"));

    get("/").produces("FIRST");
  }

  @Test
  public void multiple_routes_same_uri() {
    server.configure(routes -> routes
      .with("/").
        get(() -> "Index GET").
        post(() -> "Index POST")
      .with("/action").
        get(() -> "Action GET").
        post(() -> "Action POST")
    );

    get("/").produces("Index GET");
    post("/").produces("Index POST");
    get("/action").produces("Action GET");
    post("/action").produces("Action POST");
  }

  @Test
  public void catch_all() {
    server.configure(routes -> routes.catchAll("HELLO"));

    get("/any").produces("HELLO");
    get("/random").produces("HELLO");
  }

  @Test
  public void includes() {
    server.configure(routes -> routes.
      get("/", "MAIN").
      include(moreRoutes -> moreRoutes.get("/more", "MORE")).
      include(EvenMoreRoutes.class));

    get("/").produces("MAIN");
    get("/more").produces("MORE");
    get("/evenMore").produces("EVEN_MORE");
  }

  public static class EvenMoreRoutes implements Configuration {
    private String response = "EVEN_MORE";

    @Override
    public void configure(Routes routes) {
      routes.get("/evenMore", () -> response);
    }
  }
}
