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

import net.codestory.http.WebServer;
import net.codestory.http.misc.Env;
import net.codestory.simplelenium.SeleniumTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LiveReloadTest extends SeleniumTest {
  WebServer webServer;

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void startServer() {
    webServer = new WebServer() {
      @Override
      protected Env createEnv() {
        return Env.dev().withWorkingDir(temp.getRoot());
      }
    }.startOnRandomPort();
  }

  @After
  public void tearDown() {
    if (webServer != null) {
      webServer.stop();
    }
  }

  @Override
  protected String getDefaultBaseUrl() {
    return "http://localhost:" + webServer.port();
  }

  @Test
  public void change_file_and_refresh() throws IOException {
    File app = temp.newFolder("app");
    File index = new File(app, "changing.html");

    write(index, "---\nlayout: default\n---\n\n<h1>Hello</h1>");
    goTo("/changing.html");
    find("h1").should().contain("Hello");

    write(index, "---\nlayout: default\n---\n\n<h1>Changed</h1>");
    goTo("/changing.html");
    find("h1").should().contain("Changed");
  }

  static void write(File file, String content) throws IOException {
    Files.write(file.toPath(), content.getBytes(UTF_8));
  }
}
