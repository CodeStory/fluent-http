package net.codestory.http.types;

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class ContentTypesTest {
  ContentTypes contentTypes = new ContentTypes();

  @Test
  public void find_content_type_from_extension() {
    assertThat(contentTypes.get("index.html")).isEqualTo("text/html");
    assertThat(contentTypes.get("style.css")).isEqualTo("text/css");
    assertThat(contentTypes.get("text.txt")).isEqualTo("text/plain");
    assertThat(contentTypes.get("text.zip")).isEqualTo("application/zip");
    assertThat(contentTypes.get("image.gif")).isEqualTo("image/gif");
    assertThat(contentTypes.get("image.jpeg")).isEqualTo("image/jpeg");
    assertThat(contentTypes.get("image.jpg")).isEqualTo("image/jpeg");
    assertThat(contentTypes.get("image.png")).isEqualTo("image/png");
    assertThat(contentTypes.get("unknown")).isEqualTo("text/plain");
  }
}
