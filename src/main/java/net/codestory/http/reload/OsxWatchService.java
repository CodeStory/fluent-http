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
package net.codestory.http.reload;

import java.nio.file.Path;

import net.codestory.http.osxwatcher.Watcher;

public class OsxWatchService implements WatchServiceFacade {
  private final Path folder;

  private Watcher watcher;

  public OsxWatchService(Path folder) {
    this.folder = folder;
  }

  @Override
  public void onChange(FolderChangeListener listener) {
    watcher = new Watcher(folder.toFile(), listener::onChange);
    watcher.start();
  }

  @Override
  public void stop() {
    watcher.stop();
  }
}
