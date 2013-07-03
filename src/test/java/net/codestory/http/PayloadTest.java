package net.codestory.http;

import static java.nio.charset.StandardCharsets.*;
import static org.fest.assertions.Assertions.*;

import java.io.*;

import net.codestory.http.*;

import org.junit.*;

public class PayloadTest {
  @Test
  public void support_string() throws IOException {
    Payload payload = new Payload("Hello");

    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/html");
  }

  @Test
  public void support_byte_array() throws IOException {
    byte[] bytes = "Hello".getBytes(UTF_8);

    Payload payload = new Payload(bytes);

    assertThat(payload.getData()).isSameAs(bytes);
    assertThat(payload.getContentType()).isEqualTo("application/octet-stream");
  }

  @Test
  public void support_bean_to_json() throws IOException {
    Payload payload = new Payload(new Person("NAME", 42));

    assertThat(payload.getData()).isEqualTo("{\"name\":\"NAME\",\"age\":42}".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("application/json");
  }

  @Test
  public void support_custom_content_type() throws IOException {
    Payload payload = new Payload("text/plain", "Hello");

    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/plain");
  }

  @Test
  public void support_stream() throws IOException {
    Payload payload = new Payload("text/plain", new ByteArrayInputStream("Hello".getBytes()));

    assertThat(payload.getData()).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.getContentType()).isEqualTo("text/plain");
  }

  static class Person {
    final String name;
    final int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
