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

import net.codestory.http.misc.Sha1;

import java.nio.file.Path;

public class SourceFile {
  private final Path path;
  private final String content;

  public SourceFile(Path path, String content) {
    this.path = path;
    this.content = content;
  }

  public boolean hasExtension(String extension) {
    return path.toString().endsWith(extension);
  }

  public String getFileName() {
    return path.getFileName().toString(); // TODO
  }

  public String sha1() {
    return Sha1.of(content);
  }

  public Path getPath() {
    return path;
  }

  public String getContent() {
    return content;
  }

  public String getSource() {
    return content;
  }
}
