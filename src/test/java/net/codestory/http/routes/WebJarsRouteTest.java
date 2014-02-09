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
package net.codestory.http.routes;

import static org.assertj.core.api.Assertions.*;

import java.net.*;

import org.junit.*;

public class WebJarsRouteTest {
  @Test
  public void dont_use_minified_asset_in_dev_mode() {
    WebJarsRoute route = new WebJarsRoute(false);

    URL url = route.findUrl("/webjars/fakewebjar/1.0/framework.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/framework.js");

    url = route.findUrl("/webjars/fakewebjar/1.0/framework.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/framework.js");
  }

  @Test
  public void non_minified_version_doesnt_exists() {
    WebJarsRoute route = new WebJarsRoute(false);

    URL url = route.findUrl("/webjars/fakewebjar/1.0/only-minified.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/only-minified.min.js");

    url = route.findUrl("/webjars/fakewebjar/1.0/only-minified.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/only-minified.min.js");
  }

  @Test
  public void use_minified_asset_in_production_mode() {
    WebJarsRoute route = new WebJarsRoute(true);

    URL url = route.findUrl("/webjars/fakewebjar/1.0/framework.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/framework.min.js");

    url = route.findUrl("/webjars/fakewebjar/1.0/framework.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/framework.min.js");
  }

  @Test
  public void minified_version_doesnt_exists() {
    WebJarsRoute route = new WebJarsRoute(true);

    URL url = route.findUrl("/webjars/fakewebjar/1.0/fake.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/fake.js");

    url = route.findUrl("/webjars/fakewebjar/1.0/fake.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/fakewebjar/1.0/fake.js");
  }
}
