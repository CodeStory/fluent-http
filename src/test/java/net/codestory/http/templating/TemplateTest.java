package net.codestory.http.templating;

import static org.fest.assertions.Assertions.*;

import java.util.*;

import org.junit.*;

public class TemplateTest {
  @Test
  public void render() {
    assertThat(new Template("classpath:web/0variable.txt").render()).isEqualTo("0 variables");
    assertThat(new Template("classpath:web/1variable.txt").render("name", "Bob")).isEqualTo("Hello Bob");
    assertThat(new Template("classpath:web/2variables.txt").render("verb", "Hello", "name", "Bob")).isEqualTo("Hello Bob");
    assertThat(new Template("classpath:web/2variables.txt").render(new HashMap<String, String>() {{
      put("verb", "Hello");
      put("name", "Bob");
    }})).isEqualTo("Hello Bob");
  }
}
