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
package net.codestory.http.reload;

import java.util.concurrent.atomic.AtomicBoolean;

import net.codestory.http.Configuration;
import net.codestory.http.logs.Logs;
import net.codestory.http.misc.Env;
import net.codestory.http.routes.RouteCollection;

class ReloadingRoutesProvider implements RoutesProvider {
  private final Env env;
  private final Configuration configuration;
  private final AtomicBoolean dirty;

  private MultiFolderWatcher fileWatcher;
  private RouteCollection routes;

  ReloadingRoutesProvider(Env env, Configuration configuration) {
    this.env = env;
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      Logs.reloadingConfiguration();

      routes = new RouteCollection(env);
      try {
        routes.configure(configuration);

        if (fileWatcher == null) {
          fileWatcher = new MultiFolderWatcher(env.foldersToWatch(), () -> dirty.set(true));
        }

        fileWatcher.ensureStarted();
      } catch (Exception e) {
        Logs.unableToConfigureRoutes(e);
      }

      dirty.set(false);
    }

    return routes;
  }
}
