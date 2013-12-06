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
package net.codestory.http.io;

import static java.nio.file.FileVisitResult.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public interface FileVisitors {
  static SimpleFileVisitor<Path> onFile(FileAction action) {
    return new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        action.accept(file);
        return CONTINUE;
      }
    };
  }

  static SimpleFileVisitor<Path> onDirectory(FileAction action) {
    return new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
        action.accept(dir);
        return CONTINUE;
      }
    };
  }

  interface FileAction {
    void accept(Path path) throws IOException;
  }
}
