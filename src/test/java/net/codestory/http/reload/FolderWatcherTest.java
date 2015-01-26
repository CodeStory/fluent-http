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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FolderWatcherTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void watch_folder() throws IOException {
    FolderChangeListener listener = mock(FolderChangeListener.class);
    File folder = temp.newFolder();
    File parent = new File(folder, "folder");
    parent.mkdirs();

    FolderWatcher watcher = new FolderWatcher(folder.toPath(), listener);
    watcher.ensureStarted();

    new File(folder, "file").createNewFile();

    verify(listener, timeout(5000)).onChange();

    watcher.stop();
  }
}