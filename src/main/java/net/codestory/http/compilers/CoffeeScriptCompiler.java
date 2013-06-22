package net.codestory.http.compilers;

import static java.util.Arrays.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import org.jcoffeescript.*;

public class CoffeeScriptCompiler {
  public String compile(Path path) throws IOException {
    try {
      String coffee = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

      return new JCoffeeScriptCompiler(asList(Option.BARE)).compile(coffee);
    } catch (JCoffeeScriptCompileException e) {
      throw new IOException("Unable to compile less file", e);
    }
  }
}
