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
package net.codestory.http.browser;

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import net.codestory.simplelenium.FluentTest;
import net.codestory.simplelenium.rules.TakeSnapshot;
import org.junit.Rule;
import org.junit.Test;

public class ErrorPageTest extends AbstractProdWebServerTest {
  @Rule
  public final TakeSnapshot takeSnapshot = new TakeSnapshot();

  @Test
  public void page_not_found() {
    String baseUrl = "http://localhost:" + port();

    new FluentTest(baseUrl)
      .goTo("/unknown")
      .find("h1").should().contain("Page not found");
  }
}
