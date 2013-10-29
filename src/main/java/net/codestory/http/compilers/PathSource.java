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
package net.codestory.http.compilers;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.io.*;

import com.github.sommeri.less4j.*;

class PathSource extends LessSource {
  private final Path path;
  private final String content;

  PathSource(Path path, String content) {
    this.path = path;
    this.content = content;
  }

  @Override
  public LessSource relativeSource(String filename) throws CannotReadFile, FileNotFound {
    Path relativePath = Paths.get(filename);
    if (!Resources.exists(relativePath)) {
      throw new FileNotFound();
    }

    String includeContent;
    try {
      includeContent = Resources.read(relativePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new CannotReadFile();
    }

    return new PathSource(relativePath, includeContent);
  }

  @Override
  public String getName() {
    return path.toString();
  }

  @Override
  public String getContent() {
    return content;
  }
}
