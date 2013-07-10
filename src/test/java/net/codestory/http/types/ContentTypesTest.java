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
package net.codestory.http.types;

import static net.codestory.http.types.ContentTypes.*;
import static org.fest.assertions.Assertions.*;

import java.nio.file.*;

import org.junit.*;

public class ContentTypesTest {
  @Test
  public void find_content_type_from_extension() {
    assertThat(get(Paths.get("index.html"))).isEqualTo("text/html");
    assertThat(get(Paths.get("style.css"))).isEqualTo("text/css");
    assertThat(get(Paths.get("style.less"))).isEqualTo("text/css");
    assertThat(get(Paths.get("text.txt"))).isEqualTo("text/plain");
    assertThat(get(Paths.get("text.zip"))).isEqualTo("application/zip");
    assertThat(get(Paths.get("image.gif"))).isEqualTo("image/gif");
    assertThat(get(Paths.get("image.jpeg"))).isEqualTo("image/jpeg");
    assertThat(get(Paths.get("image.jpg"))).isEqualTo("image/jpeg");
    assertThat(get(Paths.get("image.png"))).isEqualTo("image/png");
    assertThat(get(Paths.get("script.js"))).isEqualTo("application/javascript");
    assertThat(get(Paths.get("script.coffee"))).isEqualTo("application/javascript");
    assertThat(get(Paths.get("unknown"))).isEqualTo("text/plain");
  }

  @Test
  public void find_compatibility_with_templating() {
    assertThat(support_templating(Paths.get("index.html"))).isTrue();
    assertThat(support_templating(Paths.get("style.css"))).isTrue();
    assertThat(support_templating(Paths.get("style.less"))).isTrue();
    assertThat(support_templating(Paths.get("text.txt"))).isTrue();
    assertThat(support_templating(Paths.get("text.zip"))).isFalse();
    assertThat(support_templating(Paths.get("image.gif"))).isFalse();
    assertThat(support_templating(Paths.get("image.jpeg"))).isFalse();
    assertThat(support_templating(Paths.get("image.jpg"))).isFalse();
    assertThat(support_templating(Paths.get("image.png"))).isFalse();
    assertThat(support_templating(Paths.get("script.js"))).isFalse();
    assertThat(support_templating(Paths.get("script.coffee"))).isFalse();
    assertThat(support_templating(Paths.get("unknown"))).isFalse();
  }

  @Test
  public void binary() {
    assertThat(is_binary(Paths.get("index.html"))).isFalse();
    assertThat(is_binary(Paths.get("style.css"))).isFalse();
    assertThat(is_binary(Paths.get("style.less"))).isFalse();
    assertThat(is_binary(Paths.get("text.txt"))).isFalse();
    assertThat(is_binary(Paths.get("text.zip"))).isTrue();
    assertThat(is_binary(Paths.get("image.gif"))).isTrue();
    assertThat(is_binary(Paths.get("image.jpeg"))).isTrue();
    assertThat(is_binary(Paths.get("image.jpg"))).isTrue();
    assertThat(is_binary(Paths.get("image.png"))).isTrue();
    assertThat(is_binary(Paths.get("script.js"))).isFalse();
    assertThat(is_binary(Paths.get("script.coffee"))).isFalse();
    assertThat(is_binary(Paths.get("unknown"))).isTrue();
  }
}
