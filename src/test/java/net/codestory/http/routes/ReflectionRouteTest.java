package net.codestory.http.routes;

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class ReflectionRouteTest {
  @Test
  public void dont_convert() {
    assertThat(ReflectionRoute.convert("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(ReflectionRoute.convert("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void convert_integer() {
    assertThat(ReflectionRoute.convert("42", Integer.class)).isEqualTo(42);
    assertThat(ReflectionRoute.convert("42", int.class)).isEqualTo(42);
  }

  @Test
  public void convert_boolean() {
    assertThat(ReflectionRoute.convert("true", Boolean.class)).isEqualTo(true);
    assertThat(ReflectionRoute.convert("true", boolean.class)).isEqualTo(true);
    assertThat(ReflectionRoute.convert("false", Boolean.class)).isEqualTo(false);
    assertThat(ReflectionRoute.convert("false", boolean.class)).isEqualTo(false);
  }
}
