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
    assertThat(filter.matches("/")).isFalse();
    assertThat(filter.matches("/public")).isFalse();
    assertThat(filter.matches("/public/foo")).isFalse();
    assertThat(filter.matches("/public/secure/bar")).isFalse();
  }

  @Test
  public void ignore_public_extensions() {
    assertThat(filter.matches("/secure/style.less")).isFalse();
    assertThat(filter.matches("/secure/style.css")).isFalse();
  }

  @Test
  public void filter_secure_pages() {
    assertThat(filter.matches("/secure/")).isTrue();
    assertThat(filter.matches("/secure/foo")).isTrue();
    assertThat(filter.matches("/secure/foo/")).isTrue();
  }

  @Test
  public void filter_pivate_extensions() {
    assertThat(filter.matches("/secure/index.html")).isTrue();
    assertThat(filter.matches("/secure/app.js")).isTrue();
  }
}
