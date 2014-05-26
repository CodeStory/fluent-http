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

import static net.codestory.http.payload.Payload.*;

import net.codestory.http.annotations.*;
import net.codestory.http.errors.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class CORSTest extends AbstractWebServerTest {
  @Test
  public void preflight() {
    server.configure(routes -> routes
      .options("/cors", context -> {
        if (!context.isCORS() || context.isPreflight()) {
          throw new BadRequestException();
        }

        return ok()
          .withAllowOrigin("*")
          .withAllowCredentials(true)
          .withExposeHeaders("X-TOTO")
          .withMaxAge(3600);
      })
      .options("/corspf", context -> {
        if (!context.isCORS() || !context.isPreflight() || !"PUT".equals(context.header("Access-Control-Request-Method"))) {
          throw new BadRequestException();
        }

        return ok()
          .withAllowOrigin("*")
          .withAllowMethods("PUT")
          .withAllowHeaders("X-TOTO")
          .withExposeHeaders("X-TOTO")
          .withMaxAge(3600);
      })
    );

    options("/cors").produces(200);
    optionsWithHeader("/corspf", "Access-Control-Request-Method", "PUT").produces(200);
    optionsWithHeader("/corspf", "Access-Control-Request-Method", "PUT").producesHeader("Access-Control-Allow-Methods", "PUT");
  }

  @Test
  public void programmatic() {
    server.configure(routes -> routes.
      options("/origin", ok().withAllowOrigin("http://www.code-story.net")).
      options("/originall", ok().withAllowOrigin("*")).
      options("/methods", ok().withAllowMethods("GET")).
      options("/methodsmore", ok().withAllowMethods("GET", "POST")).
      options("/credentials", ok().withAllowCredentials(true)).
      options("/headers", ok().withAllowHeaders("X-TOTO")).
      options("/headersmore", ok().withAllowHeaders("X-TOTO", "X-BIDULE"))
    );

    options("/origin").producesHeader("Access-Control-Allow-Origin", "http://www.code-story.net");
    options("/originall").producesHeader("Access-Control-Allow-Origin", "*");
    options("/methods").producesHeader("Access-Control-Allow-Methods", "GET");
    options("/methodsmore").producesHeader("Access-Control-Allow-Methods", "GET, POST");
    options("/credentials").producesHeader("Access-Control-Allow-Credentials", "true");
    options("/headers").producesHeader("Access-Control-Allow-Headers", "X-TOTO");
    options("/headersmore").producesHeader("Access-Control-Allow-Headers", "X-TOTO, X-BIDULE");
  }

  @Test
  public void annotations() {
    server.configure(routes -> routes.add(CorsResource.class));

    options("/origin").producesHeader("Access-Control-Allow-Origin", "http://www.code-story.net");
    options("/originall").producesHeader("Access-Control-Allow-Origin", "*");
    options("/methods").producesHeader("Access-Control-Allow-Methods", "GET");
    options("/methodsmore").producesHeader("Access-Control-Allow-Methods", "GET, POST");
    options("/credentials").producesHeader("Access-Control-Allow-Credentials", "true");
    options("/headers").producesHeader("Access-Control-Allow-Headers", "X-TOTO");
    options("/headersmore").producesHeader("Access-Control-Allow-Headers", "X-TOTO, X-BIDULE");
  }

  public static class CorsResource {
    @Options("/origin")
    @AllowOrigin("http://www.code-story.net")
    public void route1() {
    }

    @Options("/originall")
    @AllowOrigin("*")
    public void route2() {
    }

    @Options("/methods")
    @AllowMethods("GET")
    public void route3() {
    }

    @Options("/methodsmore")
    @AllowMethods({"GET", "POST"})
    public void route4() {
    }

    @Options("/credentials")
    @AllowCredentials(true)
    public void route5() {
    }

    @Options("/headers")
    @AllowHeaders("X-TOTO")
    public void route6() {
    }

    @Options("/headersmore")
    @AllowHeaders({"X-TOTO", "X-BIDULE"})
    public void route7() {
    }
  }
}
