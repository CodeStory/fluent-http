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
package net.codestory.http.templating;

import net.codestory.http.testhelpers.*;

import org.junit.*;

public class TemplatingTest extends AbstractProdWebServerTest {
  @Test
  public void static_page() {
    get("/pageYaml").should().haveType("text/html").contain("<div>_PREFIX_TEXT_SUFFIX_</div>");
  }

  @Test
  public void model_and_view() {
    configure(routes -> routes
        .get("/hello/:name", (context, name) -> ModelAndView.of("1variable.txt", "name", name))
    );

    get("/hello/Joe").should().haveType("text/plain").contain("Hello Joe");
  }

  @Test
  public void view() {
    configure(routes -> routes
        .get("/bye", () -> ModelAndView.of("goodbye"))
    );

    get("/bye").should().haveType("text/html").contain("<p><strong>Good Bye</strong></p>");
  }

  @Test
  public void infer_template_from_route() {
    configure(routes -> routes
        .get("/1variable", Model.of("name", "Toto"))
    );

    get("/1variable").should().haveType("text/plain").contain("Hello Toto");
  }

  @Test
  public void infer_template_for_index() {
    configure(routes -> routes
        .get("/section/", Model.of("name", "Bob"))
    );

    get("/section/").should().haveType("text/plain").contain("Hello Bob");
  }

  @Test
  public void google_analytics() {
    get("/indexGoogleAnalytics.html").should().contain("<body>" + System.lineSeparator() +
      "</body>" + System.lineSeparator() +
      "<script>\n" +
      "  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
      "  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
      "  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
      "})(window,document,'script','//www.google-analytics.com/analytics.js','ga');\n" +
      "ga('create', 'UA-12345', 'auto');\n" +
      "ga('send', 'pageview');\n" +
      "</script>" + System.lineSeparator() +
      "</html>");
  }

  @Test
  public void site_variables() {
    get("/useSiteVariables.html").should().contain("Hello, customer Bob wants to buy p1 for parkr");
  }

  @Test
  public void site_tags() {
    get("/testTags").should().contain("<p>[scala]</p>\n<p>java, scala</p>\n<p>[scala]</p>");
  }
}
