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

import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;

import org.junit.Test;

public class RedirectTest extends AbstractProdWebServerTest {
  @Test
  public void seeOther() {
    configure(routes -> routes
        .get("/", Payload.seeOther("/login"))
        .get("/login", "LOGIN")
    );

    get("/").should().contain("LOGIN");
  }

  @Test
  public void can_ignore_file_extension() {
    get("/section/index").should().haveType("text/plain").contain("Hello index");
    get("/section/index.txt").should().haveType("text/plain").contain("Hello index");
  }

  @Test
  public void redirect_to_index_if_it_exists() {
    get("/section/").should().haveType("text/plain").contain("Hello index");
  }

  @Test
  public void cannot_ignore_leading_slash_in_dynamic_route() {
    configure(routes -> routes
        .get("/dynamic/", "Dynamic")
    );

    get("/dynamic/").should().haveType("text/html").contain("Dynamic");
    get("/dynamic").should().respond(404);
  }

  @Test
  public void can_ignore_leading_slash_if_index_is_present() {
    get("/section").should().haveType("text/plain").contain("Hello index");
  }
}
