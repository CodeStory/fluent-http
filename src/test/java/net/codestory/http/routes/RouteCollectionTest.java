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
package net.codestory.http.routes;

import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Resource;
import net.codestory.http.misc.Env;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class RouteCollectionTest {
  RouteCollection routeCollection = new RouteCollection(mock(Env.class));

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
  public void scan_annotated_resource() {
    RouteCollection routeCollection = spy(new RouteCollection(mock(Env.class)));

    routeCollection.autoDiscover("net.codestory.http");

    verify(routeCollection).add(StubResource.class);
  }

  @Resource
  public static class StubResource {
    @Get("/test")
    public String get() {
      return "hello";
    }
  }
}
