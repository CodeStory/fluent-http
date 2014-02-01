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

    URL url = route.findUrl("/webjars/bootstrap/3.1.0/js/bootstrap.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/bootstrap/3.1.0/js/bootstrap.js");

    url = route.findUrl("/webjars/bootstrap/3.1.0/js/bootstrap.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/bootstrap/3.1.0/js/bootstrap.js");
  }

  @Test
  public void non_minified_version_doesnt_exists() {
    WebJarsRoute route = new WebJarsRoute(false);

    URL url = route.findUrl("/webjars/coffee-script/1.7.0/coffee-script.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/coffee-script/1.7.0/coffee-script.min.js");

    url = route.findUrl("/webjars/coffee-script/1.7.0/coffee-script.js");
    assertThat(url.toString()).endsWith("/webjars/coffee-script/1.7.0/coffee-script.min.js");
  }

  @Test
  public void use_minified_asset_in_production_mode() {
    WebJarsRoute route = new WebJarsRoute(true);

    URL url = route.findUrl("/webjars/bootstrap/3.1.0/js/bootstrap.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/bootstrap/3.1.0/js/bootstrap.min.js");

    url = route.findUrl("/webjars/bootstrap/3.1.0/js/bootstrap.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/bootstrap/3.1.0/js/bootstrap.min.js");
  }

  @Test
  public void minified_version_doesnt_exists() {
    WebJarsRoute route = new WebJarsRoute(true);

    URL url = route.findUrl("/webjars/restangular/1.2.2/webjars-requirejs.min.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/restangular/1.2.2/webjars-requirejs.js");

    url = route.findUrl("/webjars/restangular/1.2.2/webjars-requirejs.js");
    assertThat(url.toString()).endsWith("/META-INF/resources/webjars/restangular/1.2.2/webjars-requirejs.js");
  }
}
