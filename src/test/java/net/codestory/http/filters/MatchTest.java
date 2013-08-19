package net.codestory.http.filters;

import static net.codestory.http.filters.Match.*;
import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class MatchTest {
  @Test
  public void better() {
    assertThat(OK.isBetter(OK)).isFalse();
    assertThat(OK.isBetter(WRONG_METHOD)).isTrue();
    assertThat(OK.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_METHOD.isBetter(OK)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_URL.isBetter(OK)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_URL)).isFalse();
  }
}
