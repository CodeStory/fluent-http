package net.codestory.http;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import net.codestory.http.misc.*;

import org.junit.*;

import com.jayway.restassured.specification.*;

public class WebServerTest {
  @Rule
  public WebServerRule server = new WebServerRule();

  @Test
  public void start_server() {
    server.get("/", () -> "Hello World");

    expect().body(equalTo("Hello World")).when().get("/");
  }

  @Test
  public void page_not_found() {
    expect().statusCode(404).when().get("/");
  }

  @Test
  public void support_multiple_urls() {
    server.get("/url1", () -> "Hello 1");
    server.get("/url2", () -> "Hello 2");

    expect().body(equalTo("Hello 1")).when().get("/url1");
    expect().body(equalTo("Hello 2")).when().get("/url2");
  }

  private ResponseSpecification expect() {
    return given().port(server.port()).expect();
  }
}
