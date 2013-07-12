package net.codestory.http.compilers;

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class MarkdownCompilerTest {
  MarkdownCompiler markdownCompiler = new MarkdownCompiler();

  @Test
  public void empty() {
    String html = markdownCompiler.compile("");

    assertThat(html).isEmpty();
  }

  @Test
  public void markdown_to_html() {
    String css = markdownCompiler.compile("This is **bold**");

    assertThat(css).isEqualTo("<p>This is <strong>bold</strong></p>");
  }
}
