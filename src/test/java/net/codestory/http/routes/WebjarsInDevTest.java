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
package net.codestory.http.routes;

import net.codestory.http.testhelpers.AbstractDevWebServerTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;

import static org.assertj.core.api.Assertions.assertThat;

public class WebjarsInDevTest extends AbstractDevWebServerTest {
  @Rule
  public final StandardErrorStreamLog stderr = new StandardErrorStreamLog();

  @Test
  public void webjar_css() {
    get("/webjars/bootstrap/3.3.4/css/bootstrap.min.css").should().respond(200).haveType("text/css").contain("Bootstrap v3.3.4");
  }

  @Test
  public void webjar_js() {
    get("/webjars/bootstrap/3.3.4/js/bootstrap.min.js").should().respond(200).haveType("application/javascript").contain("Bootstrap v3.3.4");
  }

  @Test
  public void unknown_webjars() {
    get("/webjars/missing.js").should().respond(404);
    get("/webjars/missing.unknown").should().respond(404);
    get("/webjars").should().respond(404);
  }

  @Test
  public void print_possible_candidates() {
    get("/webjars/missing.js").should().respond(404);

    assertThat(stderr.getLog())
      .contains("Found these webjars files with extension: .js")
      .contains("/webjars/bootstrap/3.3.4/js/bootstrap.js")
      .contains("/webjars/bootstrap/3.3.4/js/bootstrap.min.js")
      .contains("/webjars/jquery/1.11.1/jquery.js")
      .doesNotContain("/webjars/bootstrap/3.3.4/css/bootstrap.min.css");
  }

  @Test
  public void print_debug_information() {
    get("/webjars/missing.unknown").should().respond(404);

    assertThat(stderr.getLog())
      .contains("Unable to find this webjar file: /webjars/missing.unknown")
      .contains("And no webjar file has extension [.unknown]");
  }
}
