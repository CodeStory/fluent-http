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
package net.codestory.http;

import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class CatchAllTest extends AbstractProdWebServerTest {
  @Test
  public void catch_all_get() {
    configure(routes -> routes
        .catchAllGet((context) -> "Hello")
    );

    get("/").should().contain("Hello");
    get("/any").should().contain("Hello");
    post("/route").should().respond(405);
  }

  @Test
  public void catch_all_head() {
    configure(routes -> routes
        .catchAllHead((context) -> Payload.ok())
    );

    head("/").should().respond(200);
    head("/any").should().respond(200);
    post("/route").should().respond(405);
  }

  @Test
  public void catch_all_post() {
    configure(routes -> routes
        .catchAllPost((context) -> "Hello")
    );

    post("/").should().contain("Hello");
    post("/any").should().contain("Hello");
    get("/route").should().respond(405);
  }

  @Test
  public void catch_all_put() {
    configure(routes -> routes
        .catchAllPut((context) -> "Hello")
    );

    put("/").should().contain("Hello");
    put("/any").should().contain("Hello");
    post("/route").should().respond(405);
  }

  @Test
  public void catch_all_options() {
    configure(routes -> routes
        .catchAllOptions((context) -> "Hello")
    );

    options("/").should().contain("Hello");
    options("/any").should().contain("Hello");
    post("/route").should().respond(405);
  }

  @Test
  public void catch_all_delete() {
    configure(routes -> routes
        .catchAllDelete((context) -> "Hello")
    );

    delete("/").should().contain("Hello");
    delete("/any").should().contain("Hello");
    post("/route").should().respond(405);
  }
}
