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

import java.util.*;

import net.codestory.http.annotations.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class PostTest extends AbstractWebServerTest {
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

    post("/post").produces("Done");
    post("/post/Bob").produces("Done Bob");
    post("/action").produces("Done POST");
    get("/action").produces("Done GET");
    post("/person").produces("CREATED");
    post("/order/12", "name", "Book", "quantity", "42").produces("order 12 : 42xBook");
    post("/order/12", "{\"name\":\"foo\",\"quantity\":42}").produces("order 12 : 42xfoo");
    post("/get").produces(405);
    post("/index.html").produces(405);
    post("/unknown").produces(404);
  }

  @Test
  public void forms() {
    server.configure(routes -> routes.
        post("/postForm", (context) -> "CREATED " + context.get("firstName") + " " + context.get("lastName")).
        post("/postForm", (context) -> "CREATED " + context.get("firstName") + " " + context.get("lastName")).
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

    post("/postForm", "firstName", "John", "lastName", "Doe").produces("CREATED John Doe");
    post("/postFormResource", "firstName", "Jane", "lastName", "Doe").produces("CREATED Jane Doe");
    post("/postBean", "firstName", "John", "lastName", "Doe").produces("CREATED John Doe");
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
