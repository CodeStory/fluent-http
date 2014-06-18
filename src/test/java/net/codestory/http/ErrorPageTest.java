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

import static net.codestory.http.errors.NotFoundException.*;

import java.util.*;

import net.codestory.http.errors.*;
import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class ErrorPageTest extends AbstractProdWebServerTest {
  @Test
  public void not_found_exception() {
    server.configure(routes -> routes.get("/error", () -> {
      throw new NotFoundException();
    }));

    get("/error").produces(404, "text/html", "Page not found");
  }

  @Test
  public void not_found_payload() {
    server.configure(routes -> routes.get("/notfound", Payload.notFound()));

    get("/notfound").produces(404, "text/html", "Page not found");
  }

  @Test
  public void not_found_optional() {
    server.configure(routes -> routes.get("/notfound", Optional.<String>empty()));

    get("/notfound").produces(404, "text/html", "Page not found");
  }

  @Test
  public void undefined_route() {
    get("/undefined").produces(404, "text/html", "Page not found");
  }

  @Test
  public void error() {
    server.configure(routes -> routes.get("/", () -> {
      throw new RuntimeException("BUG");
    }));

    get("/").produces(500, "text/html", "An error occurred on the server");
  }

  @Test
  public void not_found_if_null() {
    server.configure(routes -> routes.get("/hello/:name", (context, name) -> {
      String result = name.equals("Bob") ? "Hello Bob" : null;
      return notFoundIfNull(result);
    }));

    get("/hello/Bob").produces("text/html", "Hello Bob");
    get("/hello/Dave").produces(404, "text/html", "Page not found");
  }

  @Test
  public void error_message_header() {
    server.configure(routes -> routes.get("/error", () -> {
      throw new RuntimeException("NASTY BUG");
    }));

    get("/error").produces(500, "text/html", "An error occurred on the server").producesHeader("reason", "NASTY BUG");
  }
}
