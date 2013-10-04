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

import static java.util.Collections.*;
import static org.jcoffeescript.Option.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.io.*;

import org.jcoffeescript.*;

import com.github.rjeschke.txtmark.*;
import com.github.sommeri.less4j.*;
import com.github.sommeri.less4j.core.*;

public enum Compiler {
  COFFEE {
    @Override
    String compile(String coffee) throws IOException {
      try {
        return new JCoffeeScriptCompiler(singletonList(BARE)).compile(coffee);
      } catch (JCoffeeScriptCompileException e) {
        throw new IOException("Unable to compile coffee", e);
      }
    }
  },
  MARKDOWN {
    @Override
    String compile(String markdown) {
      return Processor.process(markdown);
    }
  },
  LESS {
    @Override
    String compile(String less) throws IOException {
      try {
        return new ThreadUnsafeLessCompiler().compile(less).getCss();
      } catch (Less4jException e) {
        throw new IOException("Unable to compile less", e);
      }
    }
  },
  NONE {
    @Override
    String compile(String content) {
      return content;
    }
  };

  abstract String compile(String content) throws IOException;

  public static String compile(Path path, String content) throws IOException {
    String extension = extension(path);

    Compiler compiler = compilerForExtension(extension);

    return compiler.compile(content);
  }

  private static String extension(Path path) {
    return Strings.substringAfterLast(path.toString(), ".");
  }

  private static Compiler compilerForExtension(String extension) {
    switch (extension) {
      case "less":
        return LESS;
      case "coffee":
        return COFFEE;
      case "md":
      case "markdown":
        return MARKDOWN;
      default:
        return NONE;
    }
  }
}
