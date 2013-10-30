package net.codestory.http.templating;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import org.junit.*;

public class YamlParserTest {
  YamlParser parser = new YamlParser();

  @Test
  public void parse() {
    Map<String, Object> variables = parser.parse("name: Bob");

    assertThat(variables).hasSize(1).containsEntry("name", "Bob");
  }
}
