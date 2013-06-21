package net.codestory.http;

import static java.nio.charset.StandardCharsets.*;
import static org.fest.assertions.Assertions.*;

import net.codestory.http.*;

import org.junit.*;

public class PayloadTest {
  @Test
  public void support_string() {
    Payload payload = new Payload("Hello");

    assertThat(payload.data).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.contentType).isEqualTo("text/html");
  }

  @Test
  public void support_byte_array() {
    byte[] bytes = "Hello".getBytes(UTF_8);

    Payload payload = new Payload(bytes);

    assertThat(payload.data).isSameAs(bytes);
    assertThat(payload.contentType).isEqualTo("application/octet-stream");
  }

  @Test
  public void support_bean_to_json() {
    Payload payload = new Payload(new Person("NAME", 42));

    assertThat(payload.data).isEqualTo("{\"name\":\"NAME\",\"age\":42}".getBytes(UTF_8));
    assertThat(payload.contentType).isEqualTo("application/json");
  }

  @Test
  public void support_custom_content_type() {
    Payload payload = new Payload("text/plain", "Hello");

    assertThat(payload.data).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.contentType).isEqualTo("text/plain");
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
