package net.codestory.http.compilers;

import static org.xwiki.rendering.syntax.Syntax.*;

import java.io.*;

import org.xwiki.component.embed.*;
import org.xwiki.rendering.converter.*;
import org.xwiki.rendering.renderer.printer.*;

public class MarkdownCompiler {
  public String compile(String markdown) throws IOException {
    EmbeddableComponentManager componentManager = new EmbeddableComponentManager();
    componentManager.initialize(ClassLoader.getSystemClassLoader());

    try (Reader source = new StringReader(markdown)) {
      Converter converter = componentManager.getInstance(Converter.class);

      WikiPrinter printer = new DefaultWikiPrinter();
      converter.convert(source, MARKDOWN_1_0, XHTML_1_0, printer);

      return printer.toString();
    } catch (Exception e) {
      throw new IOException("Unable to compile markdown", e);
    }
  }
}
