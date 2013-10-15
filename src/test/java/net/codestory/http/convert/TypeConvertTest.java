package net.codestory.http.convert;

import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class TypeConvertTest {
  @Test
  public void dont_convert() {
    assertThat(TypeConvert.convert("TEXT", String.class)).isEqualTo("TEXT");
    assertThat(TypeConvert.convert("TEXT", Object.class)).isEqualTo("TEXT");
  }

  @Test
  public void convert_integer() {
    assertThat(TypeConvert.convert("42", Integer.class)).isEqualTo(42);
    assertThat(TypeConvert.convert("42", int.class)).isEqualTo(42);
  }

  @Test
  public void convert_boolean() {
    assertThat(TypeConvert.convert("true", Boolean.class)).isEqualTo(true);
    assertThat(TypeConvert.convert("true", boolean.class)).isEqualTo(true);
    assertThat(TypeConvert.convert("false", Boolean.class)).isEqualTo(false);
    assertThat(TypeConvert.convert("false", boolean.class)).isEqualTo(false);
  }
}
