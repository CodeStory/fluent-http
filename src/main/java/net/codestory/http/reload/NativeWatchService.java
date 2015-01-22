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

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static net.codestory.http.io.FileVisitor.onDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class NativeWatchService implements WatchServiceFacade {
  private final WatchService watcher;

  public NativeWatchService(Path folder) {
    try {
      watcher = folder.getFileSystem().newWatchService();

      walkFileTree(folder, onDirectory(dir -> dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE}, HIGH)));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to watch folder " + folder, e);
    }
  }

  @Override
  public void onChange(FolderChangeListener listener) {
    try {
      WatchKey take = watcher.take();

      boolean changed = false;
      for (WatchEvent<?> event : take.pollEvents()) {
        if (event.kind() == OVERFLOW) {
          continue;
        }

        // consume all events of this shitty API
        changed = true;
      }

      if (changed) {
        listener.onChange();
      }

      take.reset();
    } catch (InterruptedException e) {
      // Ignore
    }
  }
}
