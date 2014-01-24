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
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import net.codestory.http.annotations.*;
import net.codestory.http.errors.*;
import net.codestory.http.filters.basic.*;
import net.codestory.http.injection.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.routes.*;
import net.codestory.http.templating.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class WebServerTest extends AbstractWebServerTest {
  @Test
  public void not_found() {
    server.configure(routes -> routes.
        get("/error", () -> {
          throw new NotFoundException();
        }).
        get("/notfound", Payload.notFound()));

    get("/error").produces(404, "text/html", "Page not found");
    get("/notfound").produces(404, "text/html", "Page not found");
    get("/undefined").produces(404, "text/html", "Page not found");
  }

  @Test
  public void content_types() {
    server.configure(routes -> routes.
        get("/index", "Index").
        get("/text", new Payload("text/plain", "TEXT")).
        get("/html", new Payload("text/html", "<body>HTML</body>")).
        get("/raw", "RAW".getBytes(UTF_8)).
        get("/json", new Person("NAME", 42)));

    get("/index").produces("text/html", "Index");
    get("/text").produces("text/plain", "TEXT");
    get("/html").produces("text/html", "HTML");
    get("/raw").produces("application/octet-stream", "RAW");
    get("/json").produces("application/json", "{\"name\":\"NAME\",\"age\":42}");
  }

  @Test
  public void request_params() {
    server.configure(routes -> routes.
        get("/hello/:name", (context, name) -> "Hello " + name).
        get("/say/:what/how/:loud", (context, what, loud) -> what + " " + loud).
        get("/:one/:two/:three", (context, one, two, three) -> one + " " + two + " " + three));

    get("/hello/Dave").produces("Hello Dave");
    get("/hello/John Doe").produces("Hello John Doe");
    get("/say/HI/how/LOUD").produces("HI LOUD");
    get("/ONE/TWO/THREE").produces("ONE TWO THREE");
  }

  @Test
  public void query_params() {
    server.configure(routes -> routes.
        get("/index", "Hello").
        get("/hello?name=:name", (context, name) -> "Hello " + name).
        add(new Object() {
          @Get("/keyValues")
          public String keyValues(Map<String, String> keyValues) {
            return keyValues.toString();
          }
        }));

    get("/index?query=useless").produces("Hello");
    get("/hello?name=Dave").produces("Hello Dave");
    get("/keyValues?key1=value1&key2=value2").produces("key2=value2");
  }

  @Test
  public void annotated_resources() {
    server.configure(routes -> routes.add(new Object() {
      @Get("/hello")
      @Get("/")
      public String hello() {
        return "Hello";
      }

      @Get("/bye/:whom")
      public String bye(String whom) {
        return "Good Bye " + whom;
      }

      @Get("/add/:left/:right")
      public int add(int left, int right) {
        return left + right;
      }

      @Get("/void")
      public void empty() {
      }

      @Get("/voidJson")
      @Produces("application/json")
      public void emptyJson() {
      }

      @Get("/1variable")
      @Produces("text/html")
      public Model helloBob() {
        return Model.of("name", "Bob");
      }

      @Get("/helloJoe")
      @Produces("text/html")
      public ModelAndView helloJoe() {
        return ModelAndView.of("1variable", "name", "Joe");
      }

      @Get("/notFound")
      public void notFound() {
        throw new NotFoundException();
      }
    }));

    get("/hello").produces("Hello");
    get("/").produces("Hello");
    get("/bye/Bob").produces("Good Bye Bob");
    get("/add/22/20").produces("application/json", "42");
    get("/void").produces(200, "text/html", "");
    get("/voidJson").produces(200, "application/json", "");
    get("/1variable").produces(200, "text/html", "Hello Bob");
    get("/helloJoe").produces(200, "text/html", "Hello Joe");
    get("/notFound").produces(404);
  }

  @Test
  public void annotated_resources_with_prefix() {
    server.configure(routes -> routes.add("/say", new Object() {
      @Get("/hello")
      public String hello() {
        return "Hello";
      }
    }));

    get("/say/hello").produces("Hello");
  }

  @Test
  public void io_streams() {
    server.configure(routes -> routes.get("/", () -> new Payload("text/html", new ByteArrayInputStream("Hello".getBytes()))));

    get("/").produces("text/html", "Hello");
  }

  @Test
  public void templates() {
    server.configure(routes -> routes.
        get("/hello/:name", (context, name) -> ModelAndView.of("1variable.txt", "name", name)).
        get("/bye", () -> ModelAndView.of("goodbye")).
        get("/1variable", Model.of("name", "Toto")).
        get("/section/", Model.of("name", "Bob")));

    get("/pageYaml").produces("text/html", "<div>_PREFIX_TEXT_SUFFIX_</div>");
    get("/hello/Joe").produces("text/plain", "Hello Joe");
    get("/bye").produces("text/html", "<p><strong>Good Bye</strong></p>");
    get("/1variable").produces("text/plain", "Hello Toto");
    get("/section/").produces("text/plain", "Hello Bob");
  }

  @Test
  public void priority_to_route() {
    server.configure(routes -> routes.get("/", "PRIORITY"));

    get("/").produces("PRIORITY");
  }

  @Test
  public void redirect() {
    server.configure(routes -> routes.
        get("/", Payload.seeOther("/login")).
        get("/login", "LOGIN").
        get("/dynamic/", "Dynamic"));

    get("/").produces("LOGIN");
    get("/section/").produces("text/plain", "Hello index");
    get("/section").produces("text/plain", "Hello index");
    get("/dynamic/").produces("text/html", "Dynamic");
    get("/dynamic").produces("text/html", "Dynamic");
  }

  @Test
  public void filter() {
    server.configure(routes -> routes.
        get("/", "NOT FILTERED").
        get("/other", "OTHER").
        filter((uri, context, nextFilter) -> {
          if ("/".equals(uri)) {
            return new Payload("text/html", "FILTERED");
          }
          return nextFilter.get();
        }));

    get("/").produces("FILTERED");
    get("/other").produces("OTHER");
  }

  @Test
  public void error() {
    server.configure(routes -> routes.get("/", () -> {
      throw new RuntimeException("BUG");
    }));

    get("/").produces(500, "text/html", "An error occurred on the server");
  }

  @Test
  public void supports_spied_resources() {
    server.configure(routes -> routes.
        add(TestResource.class).
        setIocAdapter(new Singletons() {
          @Override
          protected <T> T postProcess(T instance) {
            return spy(instance);
          }
        }));

    get("/").produces("HELLO");
  }

  @Test
  public void support_post() {
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
  public void support_put() {
    server.configure(routes -> routes.
        put("/put", () -> "Done").
        put("/putText", (context) -> context.payload()).
        add(new Object() {
          @Put("/order/:id")
          public String order(String id, Order order) {
            return "order " + id + " : " + order.quantity + "x" + order.name;
          }
        }));

    put("/put").produces("Done");
    put("/putText", "PAYLOAD").produces("PAYLOAD");
    put("/order/12", "{\"name\":\"foo\",\"quantity\":42}").produces("order 12 : 42xfoo");
  }

  @Test
  public void support_head() {
    server.configure(routes -> routes.get("/", () -> "Hello"));

    head("/").produces(200);
  }

  @Test
  public void postForm() {
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

  @Test
  public void site_variables() {
    get("/testTags").produces("<p>scala</p>\n<p>java, scala</p>\n<p>scala</p>");
  }

  @Test
  public void cookies() {
    server.configure(routes -> routes.get("/set", () -> new Payload("").withCookie("id", "Bob")));

    get("/set").producesCookie("id", "Bob");
  }

  @Test
  public void first_route_serves_first() {
    server.configure(routes -> routes.
        get("/", "FIRST").
        get("/", "SECOND"));

    get("/").produces("FIRST");
  }

  @Test
  public void catch_all() {
    server.configure(routes -> routes.catchAll("HELLO"));

    get("/any").produces("HELLO");
    get("/random").produces("HELLO");
  }

  @Test
  public void includes() {
    server.configure(routes -> routes.
        get("/", "MAIN").
        include(moreRoutes -> moreRoutes.get("/more", "MORE")).
        include(EvenMoreRoutes.class));

    get("/").produces("MAIN");
    get("/more").produces("MORE");
    get("/evenMore").produces("EVEN_MORE");
  }

  @Test
  public void basicAuth() {
    server.configure(routes -> routes.
        filter(new BasicAuthFilter("/secure", "codestory", of("jl", "polka"))).
        get("/", "Public").
        get("/secure", "Private"));

    get("/").produces(200, "text/html", "Public");
    get("/secure").produces(401).producesHeader("WWW-Authenticate", "Basic realm=\"codestory\"");
    getWithAuth("/secure", "jl", "polka").produces(200, "text/html", "Private");
    getWithAuth("/secure", "jl", "wrongpassword").produces(401);
  }

  @Test
  public void support_delete() {
    server.configure(routes -> routes.
        delete("/delete", () -> "From route").
        add(new Object() {
          @Delete("/deleteFromResource")
          public String delete() {
            return "From resource";
          }
        }));

    delete("/delete").produces(200, "text/html", "From route");
    delete("/deleteFromResource").produces(200, "text/html", "From resource");
  }

  @Test
  public void etag_filter() {
    server.configure(routes -> routes.
        get("/", "Hello World")
    );

    get("/").produces(200, "text/html", "Hello World");
    getWithHeader("/", "If-None-Match", Md5.of("Hello World".getBytes(UTF_8))).produces(304);
  }

  @Test
  public void multiple_routes_same_uri() {
    server.configure(routes -> routes
        .with("/").
            get(() -> "Index GET").
            post(() -> "Index POST")
        .with("/action").
            get(() -> "Action GET").
            post(() -> "Action POST")
    );

    get("/").produces("Index GET");
    post("/").produces("Index POST");
    get("/action").produces("Action GET");
    post("/action").produces("Action POST");
  }

  public static class TestResource {
    @Get("/")
    public String hello() {
      return "HELLO";
    }
  }

  public static class EvenMoreRoutes implements Configuration {
    private String response = "EVEN_MORE";

    @Override
    public void configure(Routes routes) {
      routes.get("/evenMore", () -> response);
    }
  }

  static class Person {
    String name;
    int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
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
