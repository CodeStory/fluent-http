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
package net.codestory.http.samples;

import net.codestory.http.AbstractWebServer;
import net.codestory.http.internal.Handler;
import net.codestory.http.internal.HttpServerWrapper;
import net.codestory.http.internal.SimpleServerWrapper;
import net.codestory.http.websockets.WebSocketHandler;

/**
 * This is how one can configure the threads of the underlying
 * SimpleHttpServer using a custom WebServer builder.
 */
public class AnotherThreadConfigurationSample {
  public static void main(String[] args) {
    new ScalableWebServer()
      .withCountThreads(2)
      .withSelectThreads(2)
      .withWebSocketThreads(1)
      .startOnRandomPort();
  }

  public static class ScalableWebServer extends AbstractWebServer<ScalableWebServer> {
    private int count = 8;
    private int select = 1;
    private int webSocketThreads = 10;

    public ScalableWebServer withCountThreads(int count) {
      this.count = count;
      return this;
    }

    public ScalableWebServer withSelectThreads(int select) {
      this.select = select;
      return this;
    }

    public ScalableWebServer withWebSocketThreads(int webSocketThreads) {
      this.webSocketThreads = webSocketThreads;
      return this;
    }

    @Override
    protected HttpServerWrapper createHttpServer(Handler httpHandler, WebSocketHandler webSocketHandler) {
      return new SimpleServerWrapper(httpHandler, webSocketHandler, count, select, webSocketThreads);
    }
  }
}
