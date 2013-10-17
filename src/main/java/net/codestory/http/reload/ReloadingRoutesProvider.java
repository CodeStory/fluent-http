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

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

import net.codestory.http.*;
import net.codestory.http.routes.*;

import com.sun.nio.file.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final Configuration configuration;
  private RouteCollection routes;

  ReloadingRoutesProvider(Configuration configuration) {
    this.configuration = configuration;
    reload();
    startClassChangeWatcher(Paths.get("target/classes/"));
  }

  @Override
  public synchronized RouteCollection get() {
    return routes;
  }

  private synchronized void reload() {
    System.out.println("Reloading configuration");

    this.routes = new RouteCollection();
    configuration.configure(routes);
  }

  private void startClassChangeWatcher(Path path) {
    new Thread(() -> {
      WatchService watcher;
      try {
        watcher = path.getFileSystem().newWatchService();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
            dir.register(watcher, new WatchEvent.Kind[]{
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE},
                SensitivityWatchEventModifier.HIGH);
            return FileVisitResult.CONTINUE;
          }
        });
      } catch (IOException e) {
        throw new IllegalStateException("Unable to watch folder " + path);
      }

      while (true) {
        try {
          WatchKey take = watcher.take();
          boolean reload = false;
          for (WatchEvent<?> watchEvent : take.pollEvents()) {
            reload = true; // consume events of this shitty API
          }

          take.reset();
          if (reload) {
            reload();
          }
        } catch (InterruptedException e) {
        }
      }
    }).start();
  }
}
