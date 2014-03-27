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

import java.nio.file.*;
import java.util.concurrent.atomic.*;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.routes.*;

import org.slf4j.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final static Logger LOG = LoggerFactory.getLogger(ReloadingRoutesProvider.class);

  private final Configuration configuration;
  private final AtomicBoolean dirty;
  private final FolderWatcher targetFolderWatcher;
  private final FolderWatcher appFolderWatcher;

  private RouteCollection routes;

  ReloadingRoutesProvider(Configuration configuration) {
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
    this.targetFolderWatcher = new FolderWatcher(Paths.get(Resources.CLASSES_OUTPUT_DIR), ev -> dirty.set(true));
    this.appFolderWatcher = new FolderWatcher(Paths.get(Resources.ROOT), ev -> System.out.println("Change detected in app folder"));
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      LOG.info("Reloading configuration...");

      targetFolderWatcher.ensureStarted();
      if (false) {
        appFolderWatcher.ensureStarted();
      }

      routes = new RouteCollection();
      configuration.configure(routes);
      routes.addStaticRoutes();

      dirty.set(false);
    }

    return routes;
  }
}
