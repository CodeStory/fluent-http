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
    server.routes().get("/index", () -> "Hello");
    server.routes().get("/raw", () -> "RAW DATA".getBytes(StandardCharsets.UTF_8));
    server.routes().get("/json", () -> new Person("NAME", 42));
    server.routes().get("/text", () -> new Payload("text/plain", "TEXT"));

    expect().body(equalTo("Hello")).contentType("text/html").when().get("/index");
    expect().body(equalTo("RAW DATA")).contentType("application/octet-stream").when().get("/raw");
    expect().body("name", equalTo("NAME")).body("age", equalTo(42)).contentType("application/json").when().get("/json");
    expect().body(equalTo("TEXT")).contentType("text/plain").when().get("/text");
  }

  @Test
  public void request_params() {
    server.routes().get("/hello/:name", (name) -> "Hello " + name);
    server.routes().get("/other/:name", (name) -> "Other " + name);
    server.routes().get("/say/:what/how/:loud", (what, loud) -> what + " " + loud);

    expect().body(equalTo("Hello Dave")).when().get("/hello/Dave");
    expect().body(equalTo("Hello Bob")).when().get("/hello/Bob");
    expect().body(equalTo("Other Joe")).when().get("/other/Joe");
    expect().body(equalTo("HI LOUD")).when().get("/say/HI/how/LOUD");
  }

  @Test
  public void static_content() {
    server.routes().serve("web");

    expect().content(containsString("Hello From a File")).contentType("text/html").when().get("/index.html");
    expect().content(equalTo("* {}")).contentType("text/css").when().get("/assets/style.css");
    expect().content(containsString("body h1 {\n  color: red;\n}")).contentType("text/css").when().get("/assets/style.less");
    expect().statusCode(404).when().get("/../private.txt");
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
