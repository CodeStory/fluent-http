package net.codestory.http.compilers;

import java.io.*;
import java.nio.file.*;

import com.github.sommeri.less4j.*;
import com.github.sommeri.less4j.core.*;

public class LessCompiler {
  public String compile(Path path) throws IOException {
    try {
      return new ThreadUnsafeLessCompiler().compile(path.toFile()).getCss();
    } catch (Less4jException e) {
      throw new IOException("Unable to compile less file", e);
    }
  }
}
