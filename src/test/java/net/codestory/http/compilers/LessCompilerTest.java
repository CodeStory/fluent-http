package net.codestory.http.compilers;

import static org.fest.assertions.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.*;
import org.junit.rules.*;

public class LessCompilerTest {
  LessCompiler lessCompiler = new LessCompiler();

  @ClassRule
  public static TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void empty_less() throws IOException {
    String css = lessCompiler.compile(createFile(""));

    assertThat(css).isEmpty();
  }

  @Test
  public void less_to_css() throws IOException {
    String css = lessCompiler.compile(createFile("body { h1 { color: red; } }"));

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n");
  }

  static Path createFile(String content) {
    try {
      Path path = temporaryFolder.newFile().toPath();
      Files.write(path, content.getBytes());
      return path;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create file", e);
    }
  }
}
