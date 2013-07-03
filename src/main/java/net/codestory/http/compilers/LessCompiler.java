package net.codestory.http.compilers;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import com.github.sommeri.less4j.*;
import com.github.sommeri.less4j.core.*;

public class LessCompiler {
  public String compile(Path path) throws IOException {
    try {
      String less = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

      return new ThreadUnsafeLessCompiler().compile(less).getCss();
    } catch (Less4jException e) {
      throw new IOException("Unable to compile less file", e);
    }
  }
}
