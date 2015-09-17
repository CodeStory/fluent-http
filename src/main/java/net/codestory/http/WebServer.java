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

import net.codestory.http.filters.log.*;
import net.codestory.http.internal.*;
import net.codestory.http.websockets.*;

public class WebServer extends AbstractWebServer<WebServer> {
  public static void main(String[] args) {
    new WebServer()
      .configure(routes -> routes.filter(new LogRequestFilter()))
      .start();
  }

  private static final int DEFAULT_SELECT_THREADS = 1;
  private static final int DEFAULT_COUNT_THREADS = 8;
  private static final int DEFAULT_WEBSOCKET_THREADS = 10;

  public WebServer() {
    this(DEFAULT_SELECT_THREADS, DEFAULT_COUNT_THREADS, DEFAULT_WEBSOCKET_THREADS);
  }

  /**
   * @param countThreads     the number of threads used for each pool (default is 8)
   * @param selectThreads    the number of selector threads to use (default is 1)
   * @param websocketThreads the number of threads to use in router selecter (default is 10)
   */
  public WebServer(int countThreads, int selectThreads, int websocketThreads) {
    super(countThreads, selectThreads, websocketThreads);
  }

  @Override
  protected HttpServerWrapper createHttpServer(Handler httpHandler, WebSocketHandler webSocketHandler,
                                               int count, int select, int websocketThreads) {
    return new SimpleServerWrapper(httpHandler, webSocketHandler, count, select, websocketThreads);
  }
}
