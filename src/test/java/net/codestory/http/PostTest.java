/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import net.codestory.http.annotations.Post;
import net.codestory.http.constants.HttpStatus;
import net.codestory.http.payload.Payload;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.util.Map;

public class PostTest extends AbstractProdWebServerTest {
  @Test
  public void post() {
    configure(routes -> routes
        .post("/post", () -> "Done")
    );

    post("/post").should().contain("Done").respond(200);
  }

  @Test
  public void post_created() {
    configure(routes -> routes
        .post("/post", () -> new Payload("Done").withCode(HttpStatus.CREATED))
    );

    post("/post").should().contain("Done").respond(201);
  }

  @Test
  public void get_and_post_on_same_url() {
    configure(routes -> routes
        .get("/action", () -> "Done GET")
        .post("/action", () -> "Done POST")
    );

    post("/action").should().contain("Done POST");
    get("/action").should().contain("Done GET");
  }

  @Test
  public void post_params() {
    configure(routes -> routes
        .post("/post/:who", (context, who) -> "Done " + who)
    );

    post("/post/Bob").should().contain("Done Bob");
  }

  @Test
  public void post_body_as_json() {
    configure(routes -> routes
        .post("/post", (context) -> "Hello " + context.request().contentAs(Human.class).firstName)
    );

    post("/post", "{\"firstName\":\"Bob\", \"lastName\":\"Doe\"}").should().contain("Hello Bob");
  }

  @Test
  public void post_body_as_json_with_shorter_syntax() {
    configure(routes -> routes
        .post("/post", (context) -> "Hello " + context.extract(Human.class).firstName)
    );

    post("/post", "{\"firstName\":\"Bob\", \"lastName\":\"Doe\"}").should().contain("Hello Bob");
  }

  @Test
  public void annotated_resource() {
    configure(routes -> routes
        .add(new Object() {
          @Post("/person")
          @Post("/person_alt")
          public String create() {
            return "CREATED";
          }

          @Post("/order/:id")
          public String order(String id, Order order) {
            return "order " + id + " : " + order.quantity + "x" + order.name;
          }
        })
    );

    post("/person").should().contain("CREATED");
    post("/order/12", "name", "Book", "quantity", "42").should().contain("order 12 : 42xBook");
    post("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
  }

  @Test
  public void wrong_method() {
    configure(routes -> routes
        .get("/get", () -> "Done")
    );

    post("/get").should().respond(405);
    post("/index.html").should().respond(405);
  }

  @Test
  public void not_found() {
    post("/unknown").should().respond(404);
  }

  @Test
  public void post_form() {
    configure(routes -> routes.
        post("/post", context -> "CREATED " + context.get("firstName") + " " + context.get("lastName"))
    );

    post("/post", "firstName", "John", "lastName", "Doe").should().contain("CREATED John Doe");
  }

  @Test
  public void post_form_with_resource() {
    configure(routes -> routes.
        add(new Object() {
          @Post("/post")
          public String create(Map<String, String> keyValues) {
            return "CREATED " + keyValues.get("firstName") + " " + keyValues.get("lastName");
          }
        })
    );

    post("/post", "firstName", "Jane", "lastName", "Doe").should().contain("CREATED Jane Doe");
  }

  @Test
  public void post_bean() {
    configure(routes -> routes.
        add(new Object() {
          @Post("/post")
          public String create(Human human) {
            return "CREATED " + human.firstName + " " + human.lastName;
          }
        })
    );

    post("/post", "firstName", "John", "lastName", "Doe").should().contain("CREATED John Doe");
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
