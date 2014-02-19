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

import static java.nio.file.Files.*;
import static net.codestory.http.io.FileVisitor.*;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.routes.*;

import org.slf4j.*;

import com.sun.nio.file.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final static Logger LOG = LoggerFactory.getLogger(ReloadingRoutesProvider.class);

  private final Configuration configuration;
  private final AtomicBoolean dirty;

  private boolean watcherIsStarted;
  private RouteCollection routes;

  ReloadingRoutesProvider(Configuration configuration) {
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      LOG.info("Reloading configuration...");

      if (!watcherIsStarted) {
        startClassChangeWatcher(Paths.get(Resources.CLASSES_OUTPUT_DIR));
        watcherIsStarted = true;
      }

      routes = new RouteCollection();
      configuration.configure(routes);
      routes.addStaticRoutes();

      dirty.set(false);
    }

    return routes;
  }

  private void startClassChangeWatcher(Path path) {
    new Thread(() -> watchChanges(path)).start();
  }

  private void watchChanges(Path path) {
    WatchService watcher = createWatcher(path);
    reloadOnChange(watcher);
  }

  private WatchService createWatcher(Path path) {
    try {
      WatchService watcher = path.getFileSystem().newWatchService();

      walkFileTree(path, onDirectory(dir -> {
        dir.register(watcher, new WatchEvent.Kind[]{
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE},
            SensitivityWatchEventModifier.HIGH);
      }));

      return watcher;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to watch folder " + path);
    }
  }

  private void reloadOnChange(WatchService watcher) {
    while (true) {
      try {
        WatchKey take = watcher.take();
        take.pollEvents().forEach(ev -> dirty.set(true)); // consume all events of this shitty API;
        take.reset();
      } catch (InterruptedException e) {
      }
    }
  }
}
