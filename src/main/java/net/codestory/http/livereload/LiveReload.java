/**
 * Copyright (C) 2013-2014 all@code-story.net
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
package net.codestory.http.livereload;

import net.codestory.http.Configuration;
import net.codestory.http.io.ClassPaths;
import net.codestory.http.routes.Routes;

// TODO: Web Socket connections MUST use /livereload as the path to connect
//
public class LiveReload implements Configuration {
  public static final String VERSION_7 = "http://livereload.com/protocols/official-7";

  @Override
  public void configure(Routes routes) {
    routes
      .get("/livereload.js", ClassPaths.getResource("livereload/livereload.js"))
      .setWebSocketListenerFactory((session, context) -> new LiveReloadListener(session, context.env()));
  }
}
