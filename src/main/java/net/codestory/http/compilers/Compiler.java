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
import java.nio.file.*;

import org.markdown4j.*;

import com.github.rjeschke.txtmark.*;
import com.github.sommeri.less4j.*;
import com.github.sommeri.less4j.core.*;

public enum Compiler {
  COFFEE {
    @Override
    String doCompile(Path path, String coffee) throws IOException {
      return new CoffeeCompiler().compile(coffee);
    }
  },
  MARKDOWN {
    @Override
    String doCompile(Path path, String markdown) throws IOException {
      Configuration.Builder builder = Configuration.builder()
          .forceExtentedProfile()
          .registerPlugins(new TablePlugin(), new YumlPlugin(), new WebSequencePlugin(), new IncludePlugin(), new FormulaPlugin())
          .setDecorator(new ExtDecorator())
          .setCodeBlockEmitter(new CodeBlockEmitter());

      return Processor.process(markdown, builder.build());
    }
  },
  LESS {
    @Override
    String doCompile(Path path, String less) throws IOException {
      try {
        return new ThreadUnsafeLessCompiler().compile(new PathSource(path, less)).getCss();
      } catch (Less4jException e) {
        throw new IOException("Unable to compile less", e);
      }
    }
  },
  LESS_MAP {
    @Override
    String doCompile(Path path, String less) throws IOException {
      try {
        return new ThreadUnsafeLessCompiler().compile(new PathSource(path, less)).getSourceMap();
      } catch (Less4jException e) {
        throw new IOException("Unable to compile less", e);
      }
    }
  },
  NONE {
    @Override
    String doCompile(Path path, String content) {
      return content;
    }
  };

  abstract String doCompile(Path path, String content) throws IOException;

  public static String compile(Path path, String content) throws IOException {
    return compilerForPath(path).doCompile(path, content);
  }

  private static Compiler compilerForPath(Path path) {
    String name = path.toString();

    if (name.endsWith(".less")) {
      return LESS;
    } else if (name.endsWith(".css.map")) {
      return LESS_MAP;
    } else if (name.endsWith(".coffee")) {
      return COFFEE;
    } else if (name.endsWith(".md") || name.endsWith(".markdown")) {
      return MARKDOWN;
    }

    return NONE;
  }
}
