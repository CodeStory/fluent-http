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

import net.codestory.http.annotations.AllowedCredentials;
import net.codestory.http.annotations.AllowedHeaders;
import net.codestory.http.annotations.AllowedMethods;
import net.codestory.http.annotations.AllowedOrigin;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Options;
import net.codestory.http.errors.HttpException;
import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractWebServerTest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CORSTest extends AbstractWebServerTest {


    @Test
    public void cors_preflight() {
      server.configure(routes -> routes.
        options("/cors", context -> {
          if (!context.isCORS()) throw new HttpException(400);
          if (context.isPreflight()) throw new HttpException(400);
          return new Payload("")
            .withCode(200)
            .withAllowedOrigin("*")
            .withAllowedCredentials(true)
            .withExposeHeaders("X-TOTO")
            .withMaxAge(3600);
        }).
        options("/corspf", context -> {
          if (!context.isCORS()) throw new HttpException(400);
          if (!context.isPreflight()) throw new HttpException(400);
          if (!"PUT".equals(context.getHeader("Access-Control-Allow-Method"))) throw new HttpException(400);
          return new Payload("")
            .withCode(200)
            .withAllowedOrigin("*")
            .withAllowedMethods("PUT")
            .withAllowedHeaders("X-TOTO")
            .withExposeHeaders("X-TOTO")
            .withMaxAge(3600);
        })
      );

      options("/cors").produces(200);
      optionsWithHeader("/corspf", "Access-Control-Allow-Method", "PUT").produces(200);
      optionsWithHeader("/corspf", "Access-Control-Allow-Method", "PUT").producesHeader("Access-Control-Allow-Methods", "PUT");
    }

  @Test
  public void cors_programmatic() {
    server.configure(routes -> routes.
            options("/origin", new Payload("").withCode(200).withAllowedOrigin("http://www.code-story.net")).
            options("/originall", new Payload("").withCode(200).withAllowedOrigin("*")).
            options("/methods", new Payload("").withCode(200).withAllowedMethods("GET")).
            options("/methodsmore", new Payload("").withCode(200).withAllowedMethods("GET", "POST")).
            options("/credentials", new Payload("").withCode(200).withAllowedCredentials(true)).
            options("/headers", new Payload("").withCode(200).withAllowedHeaders("X-TOTO")).
            options("/headersmore", new Payload("").withCode(200).withAllowedHeaders("X-TOTO", "X-BIDULE"))
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
  public void cors_annotations() {
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
        @Options("/origin") @AllowedOrigin("http://www.code-story.net")
        public String route1() {
            return "";
        }
        @Options("/originall") @AllowedOrigin("*")
        public String route2() {
            return "";
        }
        @Options("/methods") @AllowedMethods("GET")
        public String route3() {
            return "";
        }
        @Options("/methodsmore") @AllowedMethods({"GET", "POST"})
        public String route4() {
            return "";
        }
        @Options("/credentials") @AllowedCredentials(true)
        public String route5() {
            return "";
        }
        @Options("/headers") @AllowedHeaders("X-TOTO")
        public String route6() {
            return "";
        }
        @Options("/headersmore") @AllowedHeaders({"X-TOTO", "X-BIDULE"})
        public String route7() {
            return "";
        }
    }
}
