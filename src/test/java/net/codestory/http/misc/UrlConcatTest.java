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
package net.codestory.http.misc;

import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class UrlConcatTest {
  UrlConcat urlConcat = new UrlConcat();

  @Test
  public void url() {
    assertThat(urlConcat.url("", "", "")).isEqualTo("");
    assertThat(urlConcat.url("", "", "url")).isEqualTo("/url");
    assertThat(urlConcat.url("", "", "/url")).isEqualTo("/url");
    assertThat(urlConcat.url("", "prefix", "")).isEqualTo("/prefix");
    assertThat(urlConcat.url("", "prefix", "/url")).isEqualTo("/prefix/url");
    assertThat(urlConcat.url("", "prefix/", "")).isEqualTo("/prefix/");
    assertThat(urlConcat.url("", "prefix/", "/url")).isEqualTo("/prefix/url");
    assertThat(urlConcat.url("", "/prefix/", "/url")).isEqualTo("/prefix/url");
    assertThat(urlConcat.url("/top", "/prefix/", "")).isEqualTo("/top/prefix/");
    assertThat(urlConcat.url("/top", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(urlConcat.url("/top/", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(urlConcat.url("top/", "/prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(urlConcat.url("top/", "prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(urlConcat.url("top", "prefix/", "url")).isEqualTo("/top/prefix/url");
    assertThat(urlConcat.url("", "/prefix", "")).isEqualTo("/prefix");
    assertThat(urlConcat.url("", "/prefix", "/")).isEqualTo("/prefix/");
    assertThat(urlConcat.url("", "/prefix", "?id=:id")).isEqualTo("/prefix?id=:id");
  }
}
