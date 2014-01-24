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

import net.codestory.http.templating.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class TemplatingTest extends AbstractWebServerTest {
  @Test
  public void static_page() {
    get("/pageYaml").produces("text/html", "<div>_PREFIX_TEXT_SUFFIX_</div>");
  }

  @Test
  public void model_and_view() {
    server.configure(routes -> routes.get("/hello/:name", (context, name) -> ModelAndView.of("1variable.txt", "name", name)));

    get("/hello/Joe").produces("text/plain", "Hello Joe");
  }

  @Test
  public void view() {
    server.configure(routes -> routes.get("/bye", () -> ModelAndView.of("goodbye")));

    get("/bye").produces("text/html", "<p><strong>Good Bye</strong></p>");
  }

  @Test
  public void infer_template_from_route() {
    server.configure(routes -> routes.get("/1variable", Model.of("name", "Toto")));

    get("/1variable").produces("text/plain", "Hello Toto");
  }

  @Test
  public void infer_template_for_index() {
    server.configure(routes -> routes.get("/section/", Model.of("name", "Bob")));

    get("/section/").produces("text/plain", "Hello Bob");
  }

  @Test
  public void site_variables() {
    get("/testTags").produces("<p>scala</p>\n<p>java, scala</p>\n<p>scala</p>");
  }
}
