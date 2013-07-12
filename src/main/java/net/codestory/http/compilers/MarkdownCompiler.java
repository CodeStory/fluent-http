package net.codestory.http.compilers;

import org.pegdown.*;

public class MarkdownCompiler {
  public String compile(String markdown) {
    return new PegDownProcessor().markdownToHtml(markdown);
  }
}
