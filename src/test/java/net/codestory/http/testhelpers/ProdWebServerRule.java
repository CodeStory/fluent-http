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

import static net.codestory.http.misc.MemoizingSupplier.*;

import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.misc.*;

import org.junit.rules.*;

public class ProdWebServerRule extends ExternalResource {
  private static Supplier<WebServer> server = memoize(() -> new WebServer().startOnRandomPort());

  @Override
  protected void before() throws Throwable {
    Env.setForTests(true, false, false, false);
  }

  @Override
  protected void after() {
    Env.resetForTests();
    server.get().reset();

  }

  public void configure(Configuration configuration) {
    server.get().configure(configuration);
  }

  public int port() {
    return server.get().port();
  }
}
