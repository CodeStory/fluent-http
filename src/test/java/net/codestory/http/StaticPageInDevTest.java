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

import net.codestory.http.testhelpers.AbstractDevWebServerTest;
import org.junit.Test;

public class StaticPageInDevTest extends AbstractDevWebServerTest {
  @Test
  public void coffeescript_source() {
    get("/js/script.coffee.source").should().haveType("application/javascript").contain("console.log 'Hello'");
    get("/js/anotherscript.coffee.source").should().haveType("application/javascript").contain("console.log 'foobar'");
  }

  @Test
  public void coffeescript_map_url() {
    get("/js/anotherscript.coffee").should().contain("sourceMappingURL=/js/anotherscript.coffee.map");
    get("/js/anotherscript.js").should().contain("sourceMappingURL=/js/anotherscript.coffee.map");
  }

  @Test
  public void coffeescript_map() {
    get("/js/script.coffee.map").should().haveType("text/plain")
      .contain("\"file\": \"/js/script.coffee\"")
      .contain("\"sources\": [\n  \"/js/script.coffee.source\"\n ]")
      .contain("\"mappings\": \"AAAA,OAAO,CAAC,GAAR,CAAY,OAAZ,CAAA,CAAA\"");
  }

  @Test
  public void less_source() {
    get("/assets/style.less.source").should().haveType("text/css").contain("body {\n  h1 {\n    color: red;\n  }\n}");
    get("/assets/anotherstyle.less.source").should().haveType("text/css").contain("body { h1 { color: red; } }");
  }

  @Test
  public void less_inline_map() {
    get("/assets/anotherstyle.less.map").should().respond(404);
    get("/assets/anotherstyle.css").should().contain("sourceMappingURL");
  }

  @Test
  public void serve_overriding_css() {
    get("/assets/style.css").should().contain("* {}");
  }

  @Test
  public void serve_overriding_script() {
    get("/js/script.js").should().contain("console.log('Hello');");
  }
}
