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
import static org.assertj.core.api.Assertions.*;

import net.codestory.http.filters.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class FilterTest extends AbstractWebServerTest {
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
  public void filter_class() {
    server.configure(routes -> routes.filter(CatchAll.class));

    get("/").produces("FILTERED");
  }

  @Test
  public void etag() {
    server.configure(routes -> routes.get("/", "Hello World"));

    get("/").produces(200, "text/html", "Hello World");
    getWithHeader("/", "If-None-Match", Md5.of("Hello World".getBytes(UTF_8))).produces(304);
  }

    /**
     * This method checks that filters are executed in the same order they have been declared
     */
    @Test
    public void multi_filter() {
        List<String> filters = new ArrayList<>();

        server.configure(routes -> routes.
                get("/", "NOT FILTERED").
                get("/other", "OTHER").
                filter((uri, context, nextFilter) -> {
                    filters.add("filter 1");
                    return nextFilter.get();
                })
                .filter((uri, context, nextFilter) -> {
		    filters.add("filter 2");
		    if ("/".equals(uri)) {
                        return new Payload("text/html", "FILTERED");
                    }
                    return nextFilter.get();
                }));

        get("/other").produces("OTHER");
        assertThat(filters).containsExactly("filter 1", "filter 2");
	filters.clear();
        get("/").produces("FILTERED");
	assertThat(filters).containsExactly("filter 1", "filter 2");
    }

  public static class CatchAll implements Filter {
    @Override
    public Payload apply(String uri, Context context, PayloadSupplier nextFilter) {
      return new Payload("text/html", "FILTERED");
    }
  }
}
