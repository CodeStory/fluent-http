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
package net.codestory.http;

import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class CacheTest extends AbstractProdWebServerTest {
  @Test
  public void set_etag_header() {
    configure(routes -> routes
        .get("/", "Hello")
    );

    get("/").should().respond(200).haveType("text/html").contain("Hello").haveHeader("Etag", "8b1a9953c4611296a827abf8c47804d7");
  }

  @Test
  public void dont_send_page_with_same_etag() {
    configure(routes -> routes
        .get("/", "Hello")
    );

    get("/").withHeader("If-None-Match", "8b1a9953c4611296a827abf8c47804d7").should().respond(304);
  }

  @Test
  public void recognize_quoted_syntax() {
    configure(routes -> routes
        .get("/", "Hello")
    );

    get("/").withHeader("If-None-Match", "\"8b1a9953c4611296a827abf8c47804d7\"").should().respond(304);
  }

  @Test
  public void set_last_modified() {
    configure(routes -> routes
        .get("/", new Payload("Hello").withHeader("Last-Modified", "Wed, 12 Nov 2014 17:53:14 GMT"))
    );

    get("/").withHeader("If-Modified-Since", "Wed, 12 Nov 2014 17:53:14 GMT").should().respond(200);
  }

  @Test
  public void dont_send_unmodified_page() {
    configure(routes -> routes
        .get("/", new Payload("Hello").withHeader("Last-Modified", "Wed, 12 Nov 2014 17:53:14 GMT"))
    );

    get("/").withHeader("If-Modified-Since", "Thu, 13 Nov 2014 17:53:14 GMT").should().respond(304);
  }
}
