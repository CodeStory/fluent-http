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
package net.codestory.http.filters.auth;

import static org.assertj.core.api.Assertions.*;

import net.codestory.http.security.*;

import org.junit.*;

public class CookieAuthFilterTest {
  private CookieAuthFilter filter;

  @Before
  public void create_filter() {
    filter = new CookieAuthFilter("/secure/", Users.singleUser("admin", "adminftw"), SessionIdStore.inMemory(), ".less", ".css");
  }

  @Test
  public void ignore_public_pages() {
    assertThat(filter.matches("/", null)).isFalse();
    assertThat(filter.matches("/public", null)).isFalse();
    assertThat(filter.matches("/public/foo", null)).isFalse();
    assertThat(filter.matches("/public/secure/bar", null)).isFalse();
  }

  @Test
  public void ignore_public_extensions() {
    assertThat(filter.matches("/secure/style.less", null)).isFalse();
    assertThat(filter.matches("/secure/style.css", null)).isFalse();
  }

  @Test
  public void filter_secure_pages() {
    assertThat(filter.matches("/secure/", null)).isTrue();
    assertThat(filter.matches("/secure/foo", null)).isTrue();
    assertThat(filter.matches("/secure/foo/", null)).isTrue();
  }

  @Test
  public void filter_auth_urls() {
    assertThat(filter.matches("/auth/signin", null)).isTrue();
    assertThat(filter.matches("/auth/signout", null)).isTrue();
    assertThat(filter.matches("/auth/login", null)).isTrue();
  }

  @Test
  public void filter_private_extensions() {
    assertThat(filter.matches("/secure/index.html", null)).isTrue();
    assertThat(filter.matches("/secure/app.js", null)).isTrue();
  }

  @Test
  public void robots_txt_is_public() {
    filter = new CookieAuthFilter("/", Users.singleUser("", ""));

    assertThat(filter.matches("/robots.txt", null)).isFalse();
  }
}
