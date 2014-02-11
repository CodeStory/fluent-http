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

import static com.google.inject.internal.util.$ImmutableMap.*;

import net.codestory.http.filters.basic.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class AuthenticationTest extends AbstractWebServerTest {
  @Test
  public void public_page() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("jl", "polka"))).
        get("/", "Public"));

    get("/").produces(200, "text/html", "Public");
  }

  @Test
  public void unauthorized() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("jl", "polka"))).
        get("/secure", "Private"));

    get("/secure").produces(401).producesHeader("WWW-Authenticate", "Basic realm=\"codestory\"");
  }

  @Test
  public void secured() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("jl", "polka"))).
        get("/secure", "Private"));

    getWithAuth("/secure", "jl", "polka").produces(200, "text/html", "Private");
  }

  @Test
  public void wrong_password() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("jl", "polka"))).
        get("/secure", "Private"));

    getWithAuth("/secure", "jl", "wrongpassword").produces(401);
  }

  @Test
  public void get_user_id() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("Dave", "pwd"))).
        get("/secure", context -> "Hello " + context.currentUser()));

    getWithAuth("/secure", "Dave", "pwd").produces(200, "text/html", "Hello Dave");
  }
}
