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

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.assertj.core.api.Assertions.assertThat;

public class WebjarsTest extends AbstractProdWebServerTest {
  @Rule
  public final SystemOutRule stdout = new SystemOutRule().enableLog();

  @Rule
  public final SystemErrRule stderr = new SystemErrRule().enableLog();

  @Test
  public void webjar_css() {
    get("/webjars/bootstrap/3.3.5/css/bootstrap.min.css").should().respond(200).haveType("text/css").contain("Bootstrap v3.3.5");
  }

  @Test
  public void webjar_js() {
    get("/webjars/bootstrap/3.3.5/js/bootstrap.min.js").should().respond(200).haveType("application/javascript").contain("Bootstrap v3.3.5");
  }

  @Test
  public void unknown_webjars() {
    get("/webjars/missing").should().respond(404);
    get("/webjars/").should().respond(404);
  }

  @Test
  public void dont_print_debug_logs() {
    get("/webjars/missing.js").should().respond(404);

    assertThat(stdout.getLog()).doesNotContain("Found these webjars files");
    assertThat(stderr.getLog()).doesNotContain("Found these webjars files");
  }
}
