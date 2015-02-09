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

import net.codestory.http.annotations.Get;
import net.codestory.http.filters.basic.BasicAuthFilter;
import net.codestory.http.payload.Payload;
import net.codestory.http.security.UsersList;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CustomAnnotationsTest extends AbstractProdWebServerTest {
  @Test
  public void bypass_annotation() {
    UsersList users = new UsersList.Builder()
      .addUser("user", "pwd")
      .addUser("dummy", "pwd")
      .build();

    configure(routes -> routes
        .filter(new BasicAuthFilter("/", "realm", users))
        .registerByPassAnnotation(DummyShallNotPass.class, (context, annotation) -> "dummy".equals(context.currentUser().name()) ? Payload.forbidden() : null)
        .add(new MyResource())
    );

    get("/").withAuthentication("user", "pwd").should().contain("Hello");
    get("/").withAuthentication("dummy", "pwd").should().respond(403);
  }

  @Test
  public void enrich_annotation() {
    configure(routes -> routes
        .registerEnrichAnnotation(Header.class, (payload, annotation) -> payload.withHeader(annotation.key(), annotation.value()))
        .add(new MyResource())
    );

    get("/").should().contain("Hello").haveHeader("theHeader", "theValue");
  }

  @Target(METHOD)
  @Retention(RUNTIME)
  static @interface DummyShallNotPass {
  }

  @Target(METHOD)
  @Retention(RUNTIME)
  static @interface Header {
    String key();

    String value();
  }

  static class MyResource {
    @DummyShallNotPass
    @Header(key = "theHeader", value = "theValue")
    @Get("/")
    public String hello() {
      return "Hello";
    }
  }
}
