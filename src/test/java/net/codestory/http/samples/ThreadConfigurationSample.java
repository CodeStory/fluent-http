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

import net.codestory.http.WebServer;

/**
 * This is how one can configure the threads of the underlying
 * SimpleHttpServer.
 */
public class ThreadConfigurationSample {
  public static void main(String[] args) {
    new WebServer()
      .withSelectThreads(2)
      .withThreadCount(8)
      .withWebSocketThreads(1)
      .start();
  }
}
