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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class OsxWatchServiceTest {
  File folder;

  FolderChangeListener listener = mock(FolderChangeListener.class);

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void createFolderToWatch() throws IOException {
    folder = temp.newFolder();
    File parent = new File(folder, "folder");
    parent.mkdirs();
  }

  private static boolean isMac() {
    return System.getProperty("os.name").startsWith("Mac OS X");
  }

  @Test
  public void watch_file_creation() throws IOException {
    assumeTrue(isMac());

    OsxWatchService watcher = new OsxWatchService(folder.toPath());
    watcher.onChange(listener);

    new File(folder, "file").createNewFile();

    verify(listener, timeout(2000)).onChange();

    watcher.stop();
  }

  @Test
  public void watch_file_delete() throws IOException {
    assumeTrue(isMac());

    new File(folder, "file").createNewFile();

    OsxWatchService watcher = new OsxWatchService(folder.toPath());
    watcher.onChange(listener);

    new File(folder, "file").delete();

    verify(listener, timeout(2000)).onChange();

    watcher.stop();
  }

  @Test
  public void watch_file_modify() throws IOException {
    assumeTrue(isMac());

    new File(folder, "file").createNewFile();

    OsxWatchService watcher = new OsxWatchService(folder.toPath());
    watcher.onChange(listener);

    try (BufferedWriter writer = newBufferedWriter(new File(folder, "file").toPath(), UTF_8)) {
      writer.newLine();
    }

    verify(listener, timeout(2000)).onChange();

    watcher.stop();
  }
}