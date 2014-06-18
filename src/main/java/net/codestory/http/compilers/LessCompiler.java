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

import com.github.sommeri.less4j.Less4jException;
import com.github.sommeri.less4j.core.ThreadUnsafeLessCompiler;
import net.codestory.http.misc.Env;

import java.nio.file.Path;

import static com.github.sommeri.less4j.LessCompiler.Configuration;

public class LessCompiler implements Compiler {
  Env env;

  public LessCompiler() {
    env = new Env();// TODO: inject me instead.
  }

  @Override
  public String compile(Path path, String source) {
    try {
      Configuration configuration = new Configuration();
      configuration.setLinkSourceMap(false);

      String css = new ThreadUnsafeLessCompiler().compile(new PathSource(path, source), configuration).getCss();
      if (env.prodMode()) {
        return css;
      }
      String sourceMapping = "/*# sourceMappingURL=" + path.getFileName() + ".map */";
      return css + sourceMapping;
    } catch (Less4jException e) {
      String message = cleanMessage(path, e.getMessage());
      throw new CompilerException(message);
    }
  }

  private static String cleanMessage(Path path, String message) {
    return "Unable to compile less " + path + ": " + message.replace("Could not compile less. ", "");
  }
}