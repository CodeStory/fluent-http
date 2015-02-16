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
package net.codestory.http.payload;

import static java.util.Arrays.*;

import net.codestory.http.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class CookiesTest extends AbstractProdWebServerTest {
  @Test
  public void string_cookie() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookie("id", "Bob"))
    );

    get("/set").should().haveCookie("id", "Bob");
  }

  @Test
  public void boolean_cookie() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookie("flag", true))
    );

    get("/set").should().haveCookie("flag", "true");
  }

  @Test
  public void int_cookie() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookie("int", 42))
    );

    get("/set").should().haveCookie("int", "42");
  }

  @Test
  public void long_cookie() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookie("long", Long.MAX_VALUE))
    );

    get("/set").should().haveCookie("long", "9223372036854775807");
  }

  @Test
  public void cookie() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookie(new NewCookie("key", "value")))
    );

    get("/set").should().haveCookie("key", "value");
  }

  @Test
  public void cookies_list() {
    configure(routes -> routes
        .get("/set", () -> new Payload("").withCookies(asList(new NewCookie("key1", "value1"), new NewCookie("key2", "value2"))))
    );

    get("/set").should()
      .haveCookie("key1", "value1")
      .haveCookie("key2", "value2");
  }
}
