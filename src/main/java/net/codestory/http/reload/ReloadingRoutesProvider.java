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
package net.codestory.http.reload;

import java.util.concurrent.atomic.*;

import net.codestory.http.*;
import net.codestory.http.misc.*;
import net.codestory.http.routes.*;

import org.slf4j.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final static Logger LOG = LoggerFactory.getLogger(ReloadingRoutesProvider.class);

  private final Configuration configuration;
  private final AtomicBoolean dirty;
  private final FolderWatcher classesWatcher;
  private final FolderWatcher appWatcher;

  private RouteCollection routes;

  ReloadingRoutesProvider(Env env, Configuration configuration) {
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
    this.classesWatcher = new FolderWatcher(env.classesOutputPath(), ev -> dirty.set(true));
    this.appWatcher = new FolderWatcher(env.appPath(), ev -> dirty.set(true));
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      LOG.info("Reloading configuration...");

      classesWatcher.ensureStarted();
      appWatcher.ensureStarted();

      routes = new RouteCollection();
      configuration.configure(routes);
      routes.addStaticRoutes(false);

      dirty.set(false);
    }

    return routes;
  }
}
