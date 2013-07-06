/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
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
