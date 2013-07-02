package net.codestory.http.routes;

import org.junit.*;
import org.junit.rules.*;

public class RouteCollectionTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void fail_with_invalid_static_path() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Invalid path for static content");

    new RouteCollection().serve("UNKNOWN_PATH");
  }
}
