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

import java.util.*;

import net.codestory.http.annotations.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class PostTest extends AbstractProdWebServerTest {
  @Test
  public void post() {
    server.configure(routes -> routes.
      post("/post", () -> "Done").
      get("/get", () -> "Done").
      get("/action", () -> "Done GET").
      post("/action", () -> "Done POST").
      post("/post/:who", (context, who) -> "Done " + who).
      add(new Object() {
        @Post("/person")
        @Post("/person_alt")
        public String create() {
          return "CREATED";
        }

        @Post("/order/:id")
        public String order(String id, Order order) {
          return "order " + id + " : " + order.quantity + "x" + order.name;
        }
      }));

    post("/post").should().contain("Done");
    post("/post/Bob").should().contain("Done Bob");
    post("/action").should().contain("Done POST");
    get("/action").should().contain("Done GET");
    post("/person").should().contain("CREATED");
    post("/order/12", "name", "Book", "quantity", "42").should().contain("order 12 : 42xBook");
    post("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
    post("/get").should().respond(405);
    post("/index.html").should().respond(405);
    post("/unknown").should().respond(404);
  }

  @Test
  public void forms() {
    server.configure(routes -> routes.
      post("/postForm", context -> "CREATED " + context.get("firstName") + " " + context.get("lastName")).
      post("/postForm", context -> "CREATED " + context.get("firstName") + " " + context.get("lastName")).
      add(new Object() {
        @Post("/postFormResource")
        public String create(Map<String, String> keyValues) {
          return "CREATED " + keyValues.get("firstName") + " " + keyValues.get("lastName");
        }
      }).
      add(new Object() {
        @Post("/postBean")
        public String create(Human human) {
          return "CREATED " + human.firstName + " " + human.lastName;
        }
      }));

    post("/postForm", "firstName", "John", "lastName", "Doe").should().contain("CREATED John Doe");
    post("/postFormResource", "firstName", "Jane", "lastName", "Doe").should().contain("CREATED Jane Doe");
    post("/postBean", "firstName", "John", "lastName", "Doe").should().contain("CREATED John Doe");
  }

  static class Human {
    String firstName;
    String lastName;
  }

  static class Order {
    String name;
    int quantity;
  }
}
