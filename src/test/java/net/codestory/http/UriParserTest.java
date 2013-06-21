package net.codestory.http;

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class UriParserTest {
  @Test
  public void match_uri() {
    assertThat(new UriParser("/hello/:name").matches("/hello/Bob")).isTrue();
    assertThat(new UriParser("/hello/:name").matches("/helloBob")).isFalse();
    assertThat(new UriParser("/hello/:name").matches("/")).isFalse();
    assertThat(new UriParser("/").matches("/hello")).isFalse();
    assertThat(new UriParser("/:id/suffix").matches("/12/suffix")).isTrue();
    assertThat(new UriParser("/").matches("/")).isTrue();
    assertThat(new UriParser("/").matches("/no")).isFalse();
    assertThat(new UriParser("/no").matches("/")).isFalse();
  }

  @Test
  public void find_params() {
    assertThat(new UriParser("/hello/:name").params("/hello/Bob")).containsOnly("Bob");
    assertThat(new UriParser("/hello/:name").params("/hello/Dave")).containsOnly("Dave");
    assertThat(new UriParser("/hello/:name/aged/:age").params("/hello/Dave/aged/42")).containsOnly("Dave", "42");
    assertThat(new UriParser("/hello/:name/aged/:age").params("/hello//aged/")).containsOnly("", "");
    assertThat(new UriParser("/").params("/")).isEmpty();
  }
}
