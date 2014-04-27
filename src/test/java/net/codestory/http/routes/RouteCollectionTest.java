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
package net.codestory.http.routes;

import static org.assertj.core.api.Assertions.*;

import org.junit.*;
import org.junit.rules.*;

public class RouteCollectionTest {
  RouteCollection routeCollection = new RouteCollection();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void fail_with_too_many_params() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Expected 1 parameter in /");

    routeCollection.get("/", (context, param) -> "");
  }

  @Test
  public void fail_with_too_few_params() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Expected 2 parameters in /:one/:two/:three");

    routeCollection.get("/:one/:two/:three", (context, one, two) -> "");
  }

  @Test
  public void url() {
    assertThat(RouteCollection.url("", "", "")).isEqualTo("");
    assertThat(RouteCollection.url("", "", "url")).isEqualTo("/url");
    assertThat(RouteCollection.url("", "", "/url")).isEqualTo("/url");
    assertThat(RouteCollection.url("", "prefix", "")).isEqualTo("/prefix");
    assertThat(RouteCollection.url("", "prefix", "/url")).isEqualTo("/prefix/url");
    assertThat(RouteCollection.url("", "prefix/", "")).isEqualTo("/prefix/");
    assertThat(RouteCollection.url("", "prefix/", "/url")).isEqualTo("/prefix/url");
    assertThat(RouteCollection.url("", "/prefix/", "/url")).isEqualTo("/prefix/url");
    assertThat(RouteCollection.url("/top", "/prefix/", "")).isEqualTo("/top/prefix/");
    assertThat(RouteCollection.url("/top", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(RouteCollection.url("/top/", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(RouteCollection.url("top/", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(RouteCollection.url("top/", "prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(RouteCollection.url("top", "prefix/", "url")).isEqualTo("/top/prefix/url");
  }
}
