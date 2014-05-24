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

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.util.*;

import net.codestory.http.annotations.*;
import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class GetTest extends AbstractWebServerTest {
  @Test
  public void content_types() {
    server.configure(routes -> routes.
      get("/index", "Index").
      get("/text", new Payload("text/plain", "TEXT")).
      get("/html", new Payload("text/html", "<body>HTML</body>")).
      get("/raw", "RAW".getBytes(UTF_8)).
      get("/json", new Person("NAME", 42)).
      get("/optionalIndex", Optional.of("Index")));

    get("/index").produces("text/html", "Index");
    get("/text").produces("text/plain", "TEXT");
    get("/html").produces("text/html", "HTML");
    get("/raw").produces("application/octet-stream", "RAW");
    get("/json").produces("application/json", "{\"name\":\"NAME\",\"age\":42}");
    get("/optionalIndex").produces("text/html", "Index");
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
  public void io_streams() {
    server.configure(routes -> routes.get("/", () -> new Payload("text/html", new ByteArrayInputStream("Hello".getBytes()))));

    get("/").produces("text/html", "Hello");
  }

  static class Person {
    String name;
    int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
