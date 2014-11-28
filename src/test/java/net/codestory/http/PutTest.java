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

import net.codestory.http.annotations.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class PutTest extends AbstractProdWebServerTest {
  @Test
  public void put() {
    server.configure(routes -> routes.put("/put", () -> "Done"));

    put("/put").should().contain("Done");
  }

  @Test
  public void content() {
    server.configure(routes -> routes.put("/put", context -> context.content()));

    put("/put", "Bob").should().contain("Bob");
  }

  @Test
  public void content_as_string() {
    server.configure(routes -> routes.put("/put", context -> context.contentAsString()));

    put("/put", "Joe").should().contain("Joe");
  }

  @Test
  public void resource() {
    server.configure(routes -> routes.add(new PutResource()));

    put("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
  }

  @Test
  public void resource_class() {
    server.configure(routes -> routes.add(PutResource.class));

    put("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
  }

  public static class PutResource {
    @Put("/order/:id")
    public String update(String id, Order order) {
      return "order " + id + " : " + order.quantity + "x" + order.name;
    }
  }

  static class Order {
    String name;
    int quantity;
  }
}
