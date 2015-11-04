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

import net.codestory.http.annotations.Patch;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

public class PatchTest extends AbstractProdWebServerTest {
  @Test
  public void patch() {
    configure(routes -> routes
        .patch("/patch", () -> "Done")
    );

    patch("/patch").should().contain("Done");
  }

  @Test
  public void content_as_bytes() {
    configure(routes -> routes
        .patch("/patch", context -> context.request().contentAsBytes())
    );

    patch("/patch", "Bob").should().contain("Bob");
  }

  @Test
  public void content_as_string() {
    configure(routes -> routes
        .patch("/patch", context -> context.request().content())
    );

    patch("/patch", "Joe").should().contain("Joe");
  }

  @Test
  public void resource() {
    configure(routes -> routes
        .add(new patchResource())
    );

    patch("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
  }

  @Test
  public void resource_class() {
    configure(routes -> routes
        .add(patchResource.class)
    );

    patch("/order/12", "{\"name\":\"foo\",\"quantity\":42}").should().contain("order 12 : 42xfoo");
  }

  public static class patchResource {
    @Patch("/order/:id")
    public String update(String id, Order order) {
      return "order " + id + " : " + order.quantity + "x" + order.name;
    }
  }

  static class Order {
    String name;
    int quantity;
  }
}
