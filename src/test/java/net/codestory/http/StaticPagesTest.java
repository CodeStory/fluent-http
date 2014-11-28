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

import net.codestory.http.testhelpers.*;

import org.junit.*;

public class StaticPagesTest extends AbstractProdWebServerTest {
  @Test
  public void webJars() {
    get("/webjars/bootstrap/3.3.1/css/bootstrap.min.css").should().respond(200).haveType("text/css").contain("Bootstrap v3.3.1");
    get("/webjars/bootstrap/3.3.1/js/bootstrap.min.js").should().respond(200).haveType("application/javascript").contain("Bootstrap v3.3.1");
    get("/webjars/").should().respond(404);
  }

  @Test
  public void html() {
    get("/").should().haveType("text/html").contain("Hello From a File");
    get("/index.html").should().haveType("text/html").contain("Hello From a File");
    get("/test").should().haveType("text/html").contain("TEST");
    get("/test.html").should().haveType("text/html").contain("TEST");
  }

  @Test
  public void javascript() {
    get("/js/script.js").should().haveType("application/javascript").contain("console.log('Hello');");
  }

  @Test
  public void coffeescript() {
    get("/js/script.coffee").should().haveType("application/javascript").contain("console.log('Hello');");
    get("/js/script.js").should().haveType("application/javascript").contain("console.log('Hello');");
    get("/js/anotherscript.js").should().haveType("application/javascript").contain("console.log('foobar');");
    get("/js/non-existing.js").should().respond(404);
    get("/js/non-existing.coffee").should().respond(404);
  }

  @Test
  public void literate_coffees() {
    get("/js/literate.js").should().haveType("application/javascript").contain("console.log('Hello');");
  }

  @Test
  public void css() {
    get("/assets/style.css").should().haveType("text/css").contain("* {}");
  }

  @Test
  public void less() {
    get("/assets/style.less").should().haveType("text/css").contain("body h1 {\n  color: red;\n}");
    get("/assets/anotherstyle.css").should().haveType("text/css").contain("body h1 {\n  color: red;\n}");
    get("/assets/non-existing.css").should().respond(404);
    get("/assets/non-existing.less").should().respond(404);
  }

  @Test
  public void markdown() {
    get("/hello.md").should().haveType("text/html").contain("<strong>Hello</strong>");
    get("/goodbye.markdown").should().haveType("text/html").contain("<strong>Good Bye</strong>");
  }

  @Test
  public void private_files() {
    get("/../private.txt").should().respond(404);
    get("/_config.yaml").should().respond(404);
    get("/_layouts/layout.html").should().respond(404);
    get("/unknown").should().respond(404);
    get("/js").should().respond(404);
  }
}
