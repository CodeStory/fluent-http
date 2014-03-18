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
import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractWebServerTest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CORSTest extends AbstractWebServerTest {

  @Test
  public void cors_programmatic() {
    server.configure(routes -> routes.
      get("/origin", new Payload("hello").withAllowedOrigin("http://www.code-story.net")).
      get("/originall", new Payload("hello").withAllowedOrigin("*")).
      get("/methods", new Payload("hello").withAllowedMethods("GET")).
      get("/methodsmore", new Payload("hello").withAllowedMethods("GET", "POST")).
      get("/credentials", new Payload("hello").withAllowedCredentials(true)).
      get("/headers", new Payload("hello").withAllowedHeaders("X-TOTO")).
      get("/headersmore", new Payload("hello").withAllowedHeaders("X-TOTO", "X-BIDULE"))
    );

    get("/origin").producesHeader("Access-Control-Allow-Origin", "http://www.code-story.net");
    get("/originall").producesHeader("Access-Control-Allow-Origin", "*");
    get("/methods").producesHeader("Access-Control-Allow-Methods", "GET");
    get("/methodsmore").producesHeader("Access-Control-Allow-Methods", "GET, POST");
    get("/credentials").producesHeader("Access-Control-Allow-Credentials", "true");
    get("/headers").producesHeader("Access-Control-Allow-Headers", "X-TOTO");
    get("/headersmore").producesHeader("Access-Control-Allow-Headers", "X-TOTO, X-BIDULE");
  }

  @Test
  public void cors_annotations() {
      server.configure(routes -> routes.add(CorsResource.class));

      get("/origin").producesHeader("Access-Control-Allow-Origin", "http://www.code-story.net");
      get("/originall").producesHeader("Access-Control-Allow-Origin", "*");
      get("/methods").producesHeader("Access-Control-Allow-Methods", "GET");
      get("/methodsmore").producesHeader("Access-Control-Allow-Methods", "GET, POST");
      get("/credentials").producesHeader("Access-Control-Allow-Credentials", "true");
      get("/headers").producesHeader("Access-Control-Allow-Headers", "X-TOTO");
      get("/headersmore").producesHeader("Access-Control-Allow-Headers", "X-TOTO, X-BIDULE");
  }

    public static class CorsResource {
        @Get("/origin") @AllowedOrigin("http://www.code-story.net")
        public String route1() {
            return "Hello";
        }
        @Get("/originall") @AllowedOrigin("*")
        public String route2() {
            return "Hello";
        }
        @Get("/methods") @AllowedMethods("GET")
        public String route3() {
            return "Hello";
        }
        @Get("/methodsmore") @AllowedMethods({"GET", "POST"})
        public String route4() {
            return "Hello";
        }
        @Get("/credentials") @AllowedCredentials(true)
        public String route5() {
            return "Hello";
        }
        @Get("/headers") @AllowedHeaders("X-TOTO")
        public String route6() {
            return "Hello";
        }
        @Get("/headersmore") @AllowedHeaders({"X-TOTO", "X-BIDULE"})
        public String route7() {
            return "Hello";
        }
    }
}
