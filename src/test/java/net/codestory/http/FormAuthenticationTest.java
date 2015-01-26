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

import net.codestory.http.filters.auth.CookieAuthFilter;
import net.codestory.http.security.Users;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import net.codestory.simplelenium.FluentTest;
import org.junit.Test;

public class FormAuthenticationTest extends AbstractProdWebServerTest {
  @Test
  public void redirect_after_login() {
    configure(routes -> routes
        .filter(new CookieAuthFilter("/secure", Users.singleUser("jl", "polka")))
        .get("/auth/login", "<form method=\"post\" action=\"/auth/signin\">\n" +
          "    <input name=\"login\" id=\"login\">\n" +
          "    <input type=\"password\" name=\"password\" id=\"password\">\n" +
          "    <button type=\"submit\" id=\"submit\">Sign in</button>\n" +
          "</form>")
        .get("/secure", "<h1>Private</h1>")
    );

    new FluentTest("http://localhost:" + port())
      .goTo("/secure")
      .find("#login").fill("jl")
      .find("#password").fill("polka")
      .find("#submit").click()
      .find("h1").should().contain("Private");
  }
}
