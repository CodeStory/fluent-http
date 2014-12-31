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
package net.codestory.http.compilers;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import net.codestory.http.io.*;

import com.github.sommeri.less4j.*;

class PathSource extends LessSource {
  private final Resources resources;
  private final SourceFile sourceFile;

  PathSource(Resources resources, SourceFile sourceFile) {
    this.resources = resources;
    this.sourceFile = sourceFile;
  }

  @Override
  public LessSource relativeSource(String filename) throws CannotReadFile, FileNotFound {
    if (filename.startsWith("/webjars/")) {
      URL webjarResource = ClassPaths.getResource("META-INF/resources" + filename);
      if (webjarResource == null) {
        throw new FileNotFound();
      }

      return new LessSource.URLSource(webjarResource);
    }

    Path relativePath = Paths.get(filename);
    if (!resources.exists(relativePath)) {
      throw new FileNotFound();
    }

    try {
      return new PathSource(resources, resources.sourceFile(relativePath));
    } catch (IOException e) {
      throw new CannotReadFile();
    }
  }

  @Override
  public String getName() {
    return Resources.toUnixString(sourceFile.getPath());
  }

  @Override
  public String getContent() {
    return sourceFile.getContent();
  }

  @Override
  public byte[] getBytes() throws CannotReadFile, FileNotFound {
    if (!resources.exists(sourceFile.getPath())) {
      throw new FileNotFound();
    }

    try {
      return resources.readBytes(sourceFile.getPath());
    } catch (IOException e) {
      throw new CannotReadFile();
    }
  }
}
