package net.codestory.http.payload;

import static java.nio.charset.StandardCharsets.*;
import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class PayloadConverterTest {
  PayloadConverter payloadConverter = new PayloadConverter();

  @Test
  public void support_string() {
    Payload payload = payloadConverter.convert("Hello");

    assertThat(payload.data).isEqualTo("Hello".getBytes(UTF_8));
    assertThat(payload.contentType).isEqualTo("text/html");
  }

  @Test
  public void support_byte_array() {
    byte[] bytes = "Hello".getBytes(UTF_8);

    Payload payload = payloadConverter.convert(bytes);

    assertThat(payload.data).isSameAs(bytes);
    assertThat(payload.contentType).isEqualTo("application/octet-stream");
  }

  @Test
  public void support_bean_to_json() {
    Payload payload = payloadConverter.convert(new Person("NAME", 42));

    assertThat(payload.data).isEqualTo("{\"name\":\"NAME\",\"age\":42}".getBytes(UTF_8));
    assertThat(payload.contentType).isEqualTo("application/json");
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
