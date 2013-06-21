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
  public void not_found() {
    expect().statusCode(404).when().get("/");
  }

  @Test
  public void content_types() {
    server.get("/index", () -> "Hello");
    server.get("/raw", () -> "RAW DATA".getBytes(StandardCharsets.UTF_8));
    server.get("/json", () -> new Person("NAME", 42));
    server.get("/text", () -> new Payload("text/plain", "TEXT"));

    expect().body(equalTo("Hello")).contentType("text/html").when().get("/index");
    expect().body(equalTo("RAW DATA")).contentType("application/octet-stream").when().get("/raw");
    expect().body("name", equalTo("NAME")).body("age", equalTo(42)).contentType("application/json").when().get("/json");
    expect().body(equalTo("TEXT")).contentType("text/plain").when().get("/text");
  }

  @Test
  public void request_params() {
    server.get("/hello/${name}", (name) -> "Hello " + name);

    expect().body(equalTo("Hello Dave")).when().get("/hello/Dave");
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
