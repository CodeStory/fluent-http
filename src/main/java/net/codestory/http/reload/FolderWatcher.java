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

import static com.sun.nio.file.SensitivityWatchEventModifier.*;
import static java.nio.file.Files.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static net.codestory.http.io.FileVisitor.*;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;

class FolderWatcher {
  private final Path folder;
  private final Consumer<WatchEvent<?>> action;

  private boolean started;

  FolderWatcher(Path folder, Consumer<WatchEvent<?>> action) {
    this.folder = folder;
    this.action = action;
  }

  public void ensureStarted() {
    if (started) {
      return;
    }

    if (!folder.toFile().exists()) {
      return;
    }

    final WatchService watcher = createWatcher();
    new Thread(() -> onChange(watcher)).start();

    started = true;
  }

  private WatchService createWatcher() {
    try {
      WatchService watcher = folder.getFileSystem().newWatchService();

      walkFileTree(folder, onDirectory(dir -> {
        dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE}, HIGH);
      }));

      return watcher;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to watch folder " + folder);
    }
  }

  private void onChange(WatchService watcher) {
    while (true) {
      try {
        WatchKey take = watcher.take();
        take.pollEvents().forEach(action); // consume all events of this shitty API;
        take.reset();
      } catch (InterruptedException e) {
      }
    }
  }
}
