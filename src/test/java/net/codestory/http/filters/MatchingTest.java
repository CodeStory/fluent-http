package net.codestory.http.filters;

import static net.codestory.http.filters.Matching.*;
import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class MatchingTest {
  @Test
  public void better() {
    assertThat(MATCH.isBetter(MATCH)).isFalse();
    assertThat(MATCH.isBetter(WRONG_METHOD)).isTrue();
    assertThat(MATCH.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_METHOD.isBetter(MATCH)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_METHOD.isBetter(WRONG_URL)).isTrue();

    assertThat(WRONG_URL.isBetter(MATCH)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_METHOD)).isFalse();
    assertThat(WRONG_URL.isBetter(WRONG_URL)).isFalse();
  }
}
