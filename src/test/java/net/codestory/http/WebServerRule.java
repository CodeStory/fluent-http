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

import static net.codestory.http.misc.MemoizingSupplier.*;

import java.util.function.*;

import net.codestory.http.misc.*;

import org.junit.rules.*;

public class WebServerRule extends ExternalResource {
  private static Supplier<WebServer> server = memoize(() -> new WebServer().startOnRandomPort());

  private final Env env;

  private WebServerRule(Env env) {
    this.env = env;
  }

  public static WebServerRule devMode() {
    return new WebServerRule(new Env(false, false, false, false));
  }

  public static WebServerRule prodMode() {
    return new WebServerRule(new Env(true, false, false, false));
  }

  @Override
  protected void after() {
    server.get().reset();
  }

  public void configure(Configuration configuration) {
    server.get().configure(env, configuration);
  }

  public int port() {
    return server.get().port();
  }
}
