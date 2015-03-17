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
package net.codestory.http.errors;

import static net.codestory.http.errors.NotFoundException.*;

import java.util.*;

import net.codestory.http.*;
import net.codestory.http.compilers.*;
import net.codestory.http.extensions.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.templating.*;
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

  @Test
  public void custom_error_payload() {
    configure(routes -> routes
        .get("/not_found", () -> {
          return new Payload("text/html", "NOT FOUND!!!", 404);
        })
    );

    get("/not_found").should().respond(404).haveType("text/html").contain("NOT FOUND!!!");
  }

  @Test
  public void custom_error_page() {
    configure(routes -> routes
        .get("/not_found", () -> {
          throw new RuntimeException("NASTY BUG");
        })
        .setExtensions(new Extensions() {
          @Override
          public PayloadWriter createPayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers) {
            return new PayloadWriter(request, response, env, site, resources, compilers) {
              @Override
              protected Payload errorPage(Throwable e) {
                return new Payload("text/html", "A nice custom error page: " + e.getMessage(), 500);
              }
            };
          }
        })
    );

    get("/not_found").should().respond(500).haveType("text/html").contain("A nice custom error page: NASTY BUG");
  }
}
