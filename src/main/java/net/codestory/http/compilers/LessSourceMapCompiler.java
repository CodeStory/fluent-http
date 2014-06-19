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

import java.nio.file.*;

import com.github.sommeri.less4j.*;
import com.github.sommeri.less4j.core.*;

public class LessSourceMapCompiler implements Compiler {
  @Override
  public String compile(Path path, String source) {
    try {
      Path sourcePath = Paths.get(path.toString().replace(".map", ""));

      String sourceMap = new ThreadUnsafeLessCompiler().compile(new PathSource(sourcePath, source)).getSourceMap();
      // now it's time to close your eyes, correcting hardcoded things done with filename in the less compiler
      String sourcePathAsString = sourcePath.toString();
      sourceMap = sourceMap.replaceAll(".css", ".less").replaceAll(sourcePathAsString, sourcePathAsString.replace(".less", ".less.source"));
      return sourceMap;
    } catch (Less4jException e) {
      String message = cleanMessage(path, e.getMessage());
      throw new CompilerException(message);
    }
  }

  private static String cleanMessage(Path path, String message) {
    return "Unable to compile less " + path + ": " + message.replace("Could not compile less. ", "");
  }
}