/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.http.routes;

import net.codestory.http.Query;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
  public void match_uri_with_query_params() {
    assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/Bob")).isTrue();
    assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/Bob?opt=OPTION")).isTrue();
    assertThat(new UriParser("/hello?name=:name").matches("/hello?name=Dave")).isTrue();
  }

  @Test
  public void find_params() {
    assertThat(new UriParser("/hello/:name").params("/hello/Bob", null)).containsExactly("Bob");
    assertThat(new UriParser("/hello/:name").params("/hello/Dave", null)).containsExactly("Dave");
    assertThat(new UriParser("/hello/:name/aged/:age").params("/hello/Dave/aged/42", null)).containsExactly("Dave", "42");
    assertThat(new UriParser("/hello/:name/aged/:age").params("/hello//aged/", null)).containsExactly("", "");
    assertThat(new UriParser("/").params("/", null)).isEmpty();
  }

  @Test
  public void find_query_params() {
    assertThat(new UriParser("/hello/:name?opt=:option").params("/hello/Bob", query("opt", "OPTIONS"))).containsExactly("Bob", "OPTIONS");
    assertThat(new UriParser("/hello/:name?opt=:option&lang=:language").params("/hello/Bob", query("opt", "OPTIONS", "lang", "FR"))).containsExactly("Bob", "OPTIONS", "FR");
  }

  @Test
  public void params_count() {
    assertThat(UriParser.paramsCount("/hello")).isZero();
    assertThat(UriParser.paramsCount("/hello/:name")).isEqualTo(1);
    assertThat(UriParser.paramsCount("/hello/:name/:message")).isEqualTo(2);
    assertThat(UriParser.paramsCount("/hello/:url:")).isEqualTo(1);
    assertThat(UriParser.paramsCount("/hello/:name/:message?opt=:option&lang=:language")).isEqualTo(4);
  }

  @Test
  public void strip_query_params() {
    assertThat(UriParser.stripQueryParams("/hello")).isEqualTo("/hello");
    assertThat(UriParser.stripQueryParams("/hello?opt=:option")).isEqualTo("/hello");
  }

  @Test
  public void extract_query_params() {
    assertThat(UriParser.extractQueryParams("/hello")).isEmpty();
    assertThat(UriParser.extractQueryParams("/hello?")).isEmpty();
    assertThat(UriParser.extractQueryParams("/hello?opt=:option")).isEqualTo("opt=:option");
  }

  @Test
  public void dont_match_if_last_param_is_empty() {
    assertThat(new UriParser("/hello/:name").matches("/hello/")).isFalse();
    assertThat(new UriParser("/hello/:name/last").matches("/hello//last")).isTrue();
    assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/")).isFalse();
    assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/?opt=OPTION")).isFalse();
  }

  @Test
  public void compare() {
    assertThat(new UriParser("/:param")).isGreaterThan(new UriParser("/foo"));
    assertThat(new UriParser("/foo/:param")).isGreaterThan(new UriParser("/foo/bar"));
    assertThat(new UriParser("/:param/foo")).isGreaterThan(new UriParser("/foo/:param"));
    assertThat(new UriParser("/foo/:param/:param/qix")).isGreaterThan(new UriParser("/foo/:param/bar/:param"));
    assertThat(new UriParser("/foo/bar/:qix")).isGreaterThan(new UriParser("/foo/bar/bar/:qix"));
    assertThat(new UriParser("/foo/bar/qix")).isGreaterThanOrEqualTo(new UriParser("/foo"));
    assertThat(new UriParser("/end/:param:")).isGreaterThan(new UriParser("/end/:param"));

    assertThat(new UriParser("/foo")).isLessThan(new UriParser("/:param"));
    assertThat(new UriParser("/foo/bar")).isLessThan(new UriParser("/foo/:param"));
    assertThat(new UriParser("/foo/:param")).isLessThan(new UriParser("/:param/foo"));
    assertThat(new UriParser("/foo/:param/bar/:param")).isLessThan(new UriParser("/foo/:param/:param/qix"));
    assertThat(new UriParser("/foo/bar/bar/:qix")).isLessThan(new UriParser("/foo/bar/:qix"));
    assertThat(new UriParser("/foo")).isLessThanOrEqualTo(new UriParser("/foo/bar/qix"));
    assertThat(new UriParser("/end/:param")).isLessThan(new UriParser("/end/:param:"));
  }

  @Test
  public void test_last_param_matches_end_uri_with_two_colons() {
    assertThat(new UriParser("/directory/:directory:").matches("/directory/to/my/resource")).isTrue();
    assertThat(new UriParser("/directory/:directory:").params("/directory/to/my/resource", null)).containsExactly("to/my/resource");
    assertThat(new UriParser("/with/:param/in/:url:").params("/with/param/in/the/middle/of/url", null)).containsExactly("param", "the/middle/of/url");

    assertThat(new UriParser("/end/:empty:").matches("/end")).isFalse();
    assertThat(new UriParser("/end/:empty:").matches("/end/")).isFalse();
    assertThat(new UriParser("/end/:empty:").params("/end/", null)).containsExactly("");
  }

  @Test
  public void test_last_parameter_is_non_greedy() {
    assertThat(new UriParser("/simple/colon/:id").matches("/simple/colon/matches/only/one/path/parameter")).isFalse();
    assertThat(new UriParser("/simple/colon/:id").matches("/simple/colon/my_id")).isTrue();
    assertThat(new UriParser("/simple/colon/:id").params("/simple/colons/my_id", null)).containsExactly("my_id");
  }

  private static Query query(String key, String value) {
    Query query = mock(Query.class);
    when(query.get(key)).thenReturn(value);
    return query;
  }

  private static Query query(String key1, String value1, String key2, String value2) {
    Query query = mock(Query.class);
    when(query.get(key1)).thenReturn(value1);
    when(query.get(key2)).thenReturn(value2);
    return query;
  }
}
