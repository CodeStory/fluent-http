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
package net.codestory.http.filters;

import static java.nio.charset.StandardCharsets.*;

import net.codestory.http.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class FilterTest extends AbstractProdWebServerTest {
  @Test
  public void filter() {
    configure(routes -> routes
        .get("/", "NOT FILTERED")
        .get("/other", "OTHER")
        .filter((uri, context, nextFilter) -> {
          if ("/".equals(uri)) {
            return new Payload("FILTERED");
          }
          return nextFilter.get();
        })
    );

    get("/").should().contain("FILTERED");
    get("/other").should().contain("OTHER");
  }

  @Test
  public void filter_class() {
    configure(routes -> routes
        .filter(CatchAll.class)
    );

    get("/").should().contain("FILTERED");
  }

  @Test
  public void etag() {
    configure(routes -> routes
        .get("/", "Hello World")
    );

    get("/").should().respond(200).haveType("text/html").contain("Hello World");
    get("/").withHeader("If-None-Match", Md5.of("Hello World".getBytes(UTF_8))).should().respond(304);
  }

  @Test
  public void filter_are_executed_in_order_of_definition() {
    configure(routes -> routes
        .get("/", "NOT FILTERED")
        .filter((uri, context, next) -> new Payload("FILTER1>" + next.get().rawContent()))
        .filter((uri, context, next) -> new Payload("FILTER2>" + next.get().rawContent()))
    );

    get("/").should().contain("FILTER1>FILTER2>NOT FILTERED");
  }

  public static class CatchAll implements Filter {
    @Override
    public Payload apply(String uri, Context context, PayloadSupplier nextFilter) {
      return new Payload("FILTERED");
    }
  }
}
