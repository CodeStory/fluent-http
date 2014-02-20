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

import static org.mockito.Mockito.*;

import net.codestory.http.annotations.*;
import net.codestory.http.errors.*;
import net.codestory.http.injection.*;
import net.codestory.http.templating.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class AnnotationsTest extends AbstractWebServerTest {
  @Test
  public void annotated_resources() {
    server.configure(routes -> routes.add(new MyResource()));

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
  public void resources_class() {
    server.configure(routes -> routes.add(MyResource.class));

    get("/hello").produces("Hello");
  }

  public static class MyResource {
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
  }

  @Test
  public void prefix() {
    server.configure(routes -> routes.add("/say", new TestResource()));

    get("/say/hello").produces("Hello");
  }

  @Test
  public void add_with_prefix() {
    server.configure(routes -> routes.add("/say", TestResource.class));

    get("/say/hello").produces("Hello");
  }

  @Test
  public void resource_with_prefix() {
    server.configure(routes -> routes.add(ResourceWithPrefix.class));

    get("/prefix/route1").produces("Route 1");
    get("/prefix/route2").produces("Route 2");
  }

  @Test
  public void add_prefixed_resource_with_prefix() {
    server.configure(routes -> routes.add("/test", ResourceWithPrefix.class));

    get("/test/prefix/route1").produces("Route 1");
    get("/test/prefix/route2").produces("Route 2");
  }

  @Test
  public void spied_resources() {
    Singletons singletons = new Singletons() {
      @Override
      protected <T> T postProcess(T instance) {
        return spy(instance);
      }
    };

    TestResource resource = singletons.get(TestResource.class);
    when(resource.hello()).thenReturn("Hello from Spy");

    server.configure(routes -> routes.
        add(TestResource.class).
        setIocAdapter(singletons));

    get("/hello").produces("Hello from Spy");
  }

  public static class TestResource {
    @Get("/hello")
    public String hello() {
      return "Hello";
    }
  }

  @Prefix("/prefix")
  public static class ResourceWithPrefix {
    @Get("/route1")
    public String route1() {
      return "Route 1";
    }

    @Get("/route2")
    public String route2() {
      return "Route 2";
    }
  }
}
