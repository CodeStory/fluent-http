package net.codestory.http;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.nio.charset.*;

import net.codestory.http.misc.*;

import org.junit.*;

import com.jayway.restassured.specification.*;

public class WebServerTest {
  @Rule
  public WebServerRule server = new WebServerRule();

  @Test
  public void page_not_found() {
    expect().statusCode(404).when().get("/");
  }

  @Test
  public void support_html() {
    server.get("/url1", () -> "Hello 1");
    server.get("/url2", () -> "Hello 2");

    expect().body(equalTo("Hello 1")).when().contentType("text/html").get("/url1");
    expect().body(equalTo("Hello 2")).when().contentType("text/html").get("/url2");
  }

  @Test
  public void support_raw_data() {
    server.get("/raw", () -> "Hello 1".getBytes(StandardCharsets.UTF_8));

    expect().body(equalTo("Hello 1")).when().contentType("application/octet-stream").get("/raw");
  }

  @Test
  public void support_json() {
    server.get("/api", () -> new Person("NAME", 42));

    expect().body(equalTo("{\"name\":\"NAME\",\"age\":42}")).contentType("application/json").when().get("/api");
  }

  @Test
  public void support_custom_content_type() {
    server.get("/", () -> new Payload("text/plain", "Hello"));

    expect().body(equalTo("Hello")).contentType("text/plain").when().get("/");
  }

  private ResponseSpecification expect() {
    return given().port(server.port()).expect();
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
