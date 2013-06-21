package net.codestory.http;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import net.codestory.http.misc.*;

import org.junit.*;

public class WebServerTest {
  @ClassRule
  public static WebServerStarter server = new WebServerStarter();

  @Test
  public void should_start_server() {
    given().port(server.port())
        .expect().body(equalTo("Hello World"))
        .when().get("/");
  }
}
