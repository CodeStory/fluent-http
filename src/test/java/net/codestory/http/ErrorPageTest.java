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

import static net.codestory.http.errors.NotFoundException.*;

import java.util.*;

import net.codestory.http.errors.*;
import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class ErrorPageTest extends AbstractProdWebServerTest {
  @Test
  public void not_found_exception() {
    configure(routes -> routes
        .get("/error", () -> {
          throw new NotFoundException();
        })
    );

    get("/error").should().respond(404).haveType("text/html").contain("Page not found");
  }

  @Test
  public void not_found_payload() {
    configure(routes -> routes
        .get("/notfound", Payload.notFound())
    );

    get("/notfound").should().respond(404).haveType("text/html").contain("Page not found");
  }

  @Test
  public void not_found_optional() {
    configure(routes -> routes
        .get("/notfound", Optional.empty())
    );

    get("/notfound").should().respond(404).haveType("text/html").contain("Page not found");
  }

  @Test
  public void undefined_route() {
    get("/undefined").should().respond(404).haveType("text/html").contain("Page not found");
  }

  @Test
  public void error() {
    configure(routes -> routes
        .get("/", () -> {
          throw new RuntimeException("BUG");
        })
    );

    get("/").should().respond(500).haveType("text/html").contain("An error occurred on the server");
  }

  @Test
  public void not_found_if_null() {
    configure(routes -> routes
        .get("/hello/:name", (context, name) -> {
          String result = name.equals("Bob") ? "Hello Bob" : null;
          return notFoundIfNull(result);
        })
    );

    get("/hello/Bob").should().haveType("text/html").contain("Hello Bob");
    get("/hello/Dave").should().respond(404).haveType("text/html").contain("Page not found");
  }

  @Test
  public void error_message_header() {
    configure(routes -> routes
        .get("/error", () -> {
          throw new RuntimeException("NASTY BUG");
        })
    );

    get("/error").should().respond(500).haveType("text/html").contain("An error occurred on the server").haveHeader("reason", "NASTY BUG");
  }
}
