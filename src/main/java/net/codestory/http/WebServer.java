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

import static net.codestory.http.Configuration.*;

import java.io.*;

import net.codestory.http.filters.log.*;
import net.codestory.http.internal.*;

import javax.net.ssl.*;

public class WebServer extends AbstractWebServer<WebServer> {
  private final HttpServerWrapper server;

  public WebServer() {
    try {
      server = new SimpleServerWrapper(this::handle);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
    configure(NO_ROUTE); // TODO: remove
  }

  public static void main(String[] args) {
    new WebServer()
      .configure(routes -> routes.filter(new LogRequestFilter()))
      .start();
  }

  @Override
  protected void doStart(int port, SSLContext context, boolean authReq) throws Exception {
    server.start(this.port, context, authReq);
  }

  @Override
  protected void doStop() throws Exception {
    server.stop();
  }
}
