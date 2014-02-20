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
package net.codestory.http.testhelpers;

import net.codestory.http.*;

import org.junit.rules.*;

public class WebServerRule extends ExternalResource {
  private final String previousProdMode = System.getProperty("PROD_MODE");

  private static WebServer server;

  @Override
  protected void before() {
    System.setProperty("PROD_MODE", "true");
  }

  @Override
  protected void after() {
    server().reset();

    if (previousProdMode == null) {
      System.clearProperty("PROD_MODE");
    } else {
      System.setProperty("PROD_MODE", previousProdMode);
    }
  }

  public WebServer configure(Configuration configuration) {
    return server().configure(configuration);
  }

  public int port() {
    return server().port();
  }

  private static WebServer server() {
    if (server == null) {
      server = new WebServer();
      server.startOnRandomPort();
    }
    return server;
  }
}
