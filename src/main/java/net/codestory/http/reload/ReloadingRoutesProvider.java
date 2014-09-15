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

import static net.codestory.http.misc.Fluent.*;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.routes.*;

import org.slf4j.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final static Logger LOG = LoggerFactory.getLogger(ReloadingRoutesProvider.class);

  private final Configuration configuration;
  private final AtomicBoolean dirty;
  private final List<FolderWatcher> classesWatchers;
  private final FolderWatcher appWatcher;

  private RouteCollection routes;

  ReloadingRoutesProvider(Configuration configuration) {
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
    this.classesWatchers = of(classpathFolders()).map(path -> new FolderWatcher(path, ev -> dirty.set(true))).toList();
    this.appWatcher = new FolderWatcher(Env.get().appPath(), ev -> dirty.set(true));
  }

  protected List<Path> classpathFolders() {
    URL[] urls = ClassPaths.getUrls(Thread.currentThread().getContextClassLoader());
    return of(urls).map(url -> Paths.get(url.getPath())).toList();
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      LOG.info("Reloading configuration...");

      classesWatchers.forEach(FolderWatcher::ensureStarted);
      appWatcher.ensureStarted();

      routes = new RouteCollection();
      configuration.configure(routes);
      routes.addStaticRoutes(false);

      dirty.set(false);
    }

    return routes;
  }
}
