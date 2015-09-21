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
package net.codestory.http;

import net.codestory.http.filters.log.LogRequestFilter;
import net.codestory.http.internal.Handler;
import net.codestory.http.internal.HttpServerWrapper;
import net.codestory.http.internal.SimpleServerWrapper;
import net.codestory.http.websockets.WebSocketHandler;

public class WebServer extends AbstractWebServer<WebServer> {
  protected int threadCount = 8;
  protected int selectThreads = 1;
  protected int webSocketThreads = 10;

  public static void main(String[] args) {
    new WebServer()
      .configure(routes -> routes.filter(new LogRequestFilter()))
      .start();
  }

  public WebServer withThreadCount(int threadCount) {
    this.threadCount = threadCount;
    return this;
  }

  public WebServer withSelectThreads(int selectThreads) {
    this.selectThreads = selectThreads;
    return this;
  }

  public WebServer withWebSocketThreads(int webSocketThreads) {
    this.webSocketThreads = webSocketThreads;
    return this;
  }

  @Override
  protected HttpServerWrapper createHttpServer(Handler httpHandler, WebSocketHandler webSocketHandler) {
    return new SimpleServerWrapper(httpHandler, webSocketHandler, threadCount, selectThreads, webSocketThreads);
  }
}
