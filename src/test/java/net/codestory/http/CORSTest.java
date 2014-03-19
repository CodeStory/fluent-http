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

import net.codestory.http.annotations.AllowCredentials;
import net.codestory.http.annotations.AllowHeaders;
import net.codestory.http.annotations.AllowMethods;
import net.codestory.http.annotations.AllowOrigin;
import net.codestory.http.annotations.Options;
import net.codestory.http.constants.Headers;
import net.codestory.http.errors.HttpException;
import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractWebServerTest;
import org.junit.Test;

public class CORSTest extends AbstractWebServerTest {

  @Test
  public void cors_preflight() {
    server.configure(routes -> routes.
      options("/cors", context -> {
        if (!context.isCORS()) throw new HttpException(401);
        if (context.isPreflight()) throw new HttpException(402);
        return new Payload("")
          .withCode(200)
          .withAllowOrigin("*")
          .withAllowCredentials(true)
          .withExposeHeaders("X-TOTO")
          .withMaxAge(3600);
      }).
      options("/corspf", context -> {
        if (!context.isCORS()) throw new HttpException(403);
        if (!context.isPreflight()) throw new HttpException(404);
        if (!"PUT".equals(context.getHeader(Headers.ACCESS_CONTROL_REQUEST_METHOD))) throw new HttpException(405);
        return new Payload("")
           .withCode(200)
           .withAllowOrigin("*")
           .withAllowMethods("PUT")
           .withAllowHeaders("X-TOTO")
           .withExposeHeaders("X-TOTO")
           .withMaxAge(3600);
      })
    );

    options("/cors").produces(200);
    optionsWithHeader("/corspf", Headers.ACCESS_CONTROL_REQUEST_METHOD, "PUT").produces(200);
    optionsWithHeader("/corspf", Headers.ACCESS_CONTROL_REQUEST_METHOD, "PUT").producesHeader(Headers.ACCESS_CONTROL_ALLOW_METHODS, "PUT");
  }

  @Test
  public void cors_programmatic() {
    server.configure(routes -> routes.
      options("/origin", new Payload("").withCode(200).withAllowOrigin("http://www.code-story.net")).
      options("/originall", new Payload("").withCode(200).withAllowOrigin("*")).
      options("/methods", new Payload("").withCode(200).withAllowMethods("GET")).
      options("/methodsmore", new Payload("").withCode(200).withAllowMethods("GET", "POST")).
      options("/credentials", new Payload("").withCode(200).withAllowCredentials(true)).
      options("/headers", new Payload("").withCode(200).withAllowHeaders("X-TOTO")).
      options("/headersmore", new Payload("").withCode(200).withAllowHeaders("X-TOTO", "X-BIDULE"))
    );

    options("/origin").producesHeader(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.code-story.net");
    options("/originall").producesHeader(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    options("/methods").producesHeader(Headers.ACCESS_CONTROL_ALLOW_METHODS, "GET");
    options("/methodsmore").producesHeader(Headers.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST");
    options("/credentials").producesHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    options("/headers").producesHeader(Headers.ACCESS_CONTROL_ALLOW_HEADERS, "X-TOTO");
    options("/headersmore").producesHeader(Headers.ACCESS_CONTROL_ALLOW_HEADERS, "X-TOTO, X-BIDULE");
  }

  @Test
  public void cors_annotations() {
    server.configure(routes -> routes.add(CorsResource.class));

    options("/origin").producesHeader(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.code-story.net");
    options("/originall").producesHeader(Headers.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    options("/methods").producesHeader(Headers.ACCESS_CONTROL_ALLOW_METHODS, "GET");
    options("/methodsmore").producesHeader(Headers.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST");
    options("/credentials").producesHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    options("/headers").producesHeader(Headers.ACCESS_CONTROL_ALLOW_HEADERS, "X-TOTO");
    options("/headersmore").producesHeader(Headers.ACCESS_CONTROL_ALLOW_HEADERS, "X-TOTO, X-BIDULE");
  }

  public static class CorsResource {
    @Options("/origin")
    @AllowOrigin("http://www.code-story.net")
    public String route1() {
      return "";
    }

    @Options("/originall")
    @AllowOrigin("*")
    public String route2() {
      return "";
    }

    @Options("/methods")
    @AllowMethods("GET")
    public String route3() {
      return "";
    }

    @Options("/methodsmore")
    @AllowMethods({"GET", "POST"})
    public String route4() {
      return "";
    }

    @Options("/credentials")
    @AllowCredentials(true)
    public String route5() {
      return "";
    }

    @Options("/headers")
    @AllowHeaders("X-TOTO")
    public String route6() {
      return "";
    }

    @Options("/headersmore")
    @AllowHeaders({"X-TOTO", "X-BIDULE"})
    public String route7() {
      return "";
    }
  }
}
