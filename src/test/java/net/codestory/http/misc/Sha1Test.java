package net.codestory.http.misc;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.*;

import org.junit.*;

public class Sha1Test {
  @Test
  public void sha1() {
    assertThat(Sha1.of("".getBytes(StandardCharsets.UTF_8))).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
    assertThat(Sha1.of("Hello".getBytes(StandardCharsets.UTF_8))).isEqualTo("f7ff9e8b7bb2e09b70935a5d785e0cc5d9d0abf0");
  }
}
