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
package net.codestory.http.annotations;

import net.codestory.http.Context;
import net.codestory.http.Cookies;
import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.errors.NotFoundException;
import net.codestory.http.filters.basic.BasicAuthFilter;
import net.codestory.http.security.UsersList;
import net.codestory.http.templating.Model;
import net.codestory.http.templating.ModelAndView;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

public class AnnotatedResourceTest extends AbstractProdWebServerTest {
  @Test
  public void annotated_resources() {
    configure(routes -> routes
        .add(new MyResource())
    );

    get("/hello").should().contain("Hello");
    get("/").should().contain("Hello");
    get("/bye/Bob").should().contain("Good Bye Bob");
    get("/add/22/20").should().haveType("application/json").contain("42");
    get("/void").should().respond(200).haveType("text/html").contain("");
    get("/voidJson").should().respond(200).haveType("application/json").contain("");
    get("/1variable").should().respond(200).haveType("text/html").contain("Hello Bob");
    get("/helloJoe").should().respond(200).haveType("text/html").contain("Hello Joe");
    get("/notFound").should().respond(404);
  }

  @Test
  public void resources_class() {
    configure(routes -> routes
        .add(MyResource.class)
    );

    get("/hello").should().contain("Hello");
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
  public void add_resource_with_prefix() {
    configure(routes -> routes
        .add("/say", new TestResource())
    );

    get("/say/hello").should().contain("Hello");
  }

  @Test
  public void add_resource_class_with_prefix() {
    configure(routes -> routes
        .add("/say", TestResource.class)
    );

    get("/say/hello").should().contain("Hello");
  }

  @Test
  public void add_prefixed_resource() {
    configure(routes -> routes
        .add(ResourceWithPrefix.class)
    );

    get("/prefix/route1").should().contain("Route 1");
    get("/prefix/route2").should().contain("Route 2");
    get("/prefix").should().contain("Default");
  }

  @Test
  public void add_prefixed_resource_with_additional_prefix() {
    configure(routes -> routes
        .add("/test", ResourceWithPrefix.class)
    );

    get("/test/prefix/route1").should().contain("Route 1");
    get("/test/prefix/route2").should().contain("Route 2");
  }

  @Test
  public void inject_parameters() {
    configure(routes -> routes
        .add(ResourceWithInjection.class)
    );

    get("/injection/first/second").should().contain("first/second/Context/SimpleRequest/SimpleResponse/SimpleCookies");
  }

  @Test
  public void query_params() {
    configure(routes -> routes
            .add(new Object() {
              @Get("/test?param1=:param1&param2=:param2&param3=:param3&param4=:param4")
              public String test(String param1, int param2, boolean param3, long param4) {
                return String.join(", ", "param1:" + param1, "param2:" + param2, "param3:" + param3, "param4:" + param4);
              }
            })
    );

    get("/test?param1=param&param2=42&param3=true&param4=1337").should().contain("param1:param, param2:42, param3:true, param4:1337");
    get("/test").should().contain("param1:null, param2:0, param3:false, param4:0");
  }

  @Test
  public void authorize_roles() {
    UsersList users = new UsersList.Builder()
      .addUser("user", "pwd", "USER")
      .addUser("admin", "pwdpwd", "ADMIN", "USER")
      .addUser("other", "", "BUSINESS")
      .build();

    configure(routes -> routes
          .filter(new BasicAuthFilter("/secure", "realm", users))
          .add(new Object() {
            @Roles({"USER", "ADMIN"})
            @Get("/secure/index")
            public String secure() {
              return "Secure";
            }

            @Roles(value = {"USER", "ADMIN"}, allMatch = true)
            @Get("/secure/admin")
            public String superSecure() {
              return "Secure";
            }
          })
    );

    get("/secure/index").withAuthentication("admin", "pwdpwd").should().contain("Secure");
    get("/secure/index").withAuthentication("user", "pwd").should().contain("Secure");
    get("/secure/index").withAuthentication("other", "").should().respond(403);

    get("/secure/admin").withAuthentication("admin", "pwdpwd").should().contain("Secure");
    get("/secure/admin").withAuthentication("user", "pwd").should().respond(403);
    get("/secure/admin").withAuthentication("other", "").should().respond(403);
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

    @Get("")
    public String defaultRoute() {
      return "Default";
    }
  }

  public static class ResourceWithInjection {
    @Get("/injection/:param1/:param2")
    public String route(String param1, String param2, Context context, Request request, Response response, Cookies cookies) {
      return String.join("/", param1, param2, context.getClass().getSimpleName(), request.getClass().getSimpleName(), response.getClass().getSimpleName(), cookies.getClass().getSimpleName());
    }
  }
}
