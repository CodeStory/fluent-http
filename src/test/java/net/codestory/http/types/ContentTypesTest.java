/**
 * Copyright (C) 2013-2014 all@code-story.net
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
import static org.assertj.core.api.Assertions.*;

import java.nio.file.*;

import org.junit.*;

public class ContentTypesTest {
  @Test
  public void content_type_from_extension() {
    assertThat(get(Paths.get("index.html"))).isEqualTo("text/html;charset=UTF-8");
    assertThat(get(Paths.get("data.xml"))).isEqualTo("application/xml;charset=UTF-8");
    assertThat(get(Paths.get("style.css"))).isEqualTo("text/css;charset=UTF-8");
    assertThat(get(Paths.get("style.less"))).isEqualTo("text/css;charset=UTF-8");
    assertThat(get(Paths.get("style.css.map"))).isEqualTo("text/plain;charset=UTF-8");
    assertThat(get(Paths.get("text.md"))).isEqualTo("text/html;charset=UTF-8");
    assertThat(get(Paths.get("text.markdown"))).isEqualTo("text/html;charset=UTF-8");
    assertThat(get(Paths.get("text.txt"))).isEqualTo("text/plain;charset=UTF-8");
    assertThat(get(Paths.get("text.zip"))).isEqualTo("application/zip");
    assertThat(get(Paths.get("text.gz"))).isEqualTo("application/gzip");
    assertThat(get(Paths.get("text.pdf"))).isEqualTo("application/pdf");
    assertThat(get(Paths.get("image.gif"))).isEqualTo("image/gif");
    assertThat(get(Paths.get("image.jpeg"))).isEqualTo("image/jpeg");
    assertThat(get(Paths.get("image.jpg"))).isEqualTo("image/jpeg");
    assertThat(get(Paths.get("image.png"))).isEqualTo("image/png");
    assertThat(get(Paths.get("font.svg"))).isEqualTo("image/svg+xml");
    assertThat(get(Paths.get("font.eot"))).isEqualTo("application/vnd.ms-fontobject");
    assertThat(get(Paths.get("font.ttf"))).isEqualTo("application/x-font-ttf");
    assertThat(get(Paths.get("font.woff"))).isEqualTo("application/x-font-woff");
    assertThat(get(Paths.get("script.js"))).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get(Paths.get("script.coffee"))).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get(Paths.get("script.litcoffee"))).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get(Paths.get("favicon.ico"))).isEqualTo("image/x-icon");
    assertThat(get(Paths.get("unknown"))).isEqualTo("text/plain;charset=UTF-8");
  }

  @Test
  public void compatibility_with_templating() {
    assertThat(supportsTemplating(Paths.get("index.html"))).isTrue();
    assertThat(supportsTemplating(Paths.get("data.xml"))).isTrue();
    assertThat(supportsTemplating(Paths.get("test.md"))).isTrue();
    assertThat(supportsTemplating(Paths.get("test.markdown"))).isTrue();
    assertThat(supportsTemplating(Paths.get("text.txt"))).isTrue();
    assertThat(supportsTemplating(Paths.get("style.css.map"))).isFalse();
    assertThat(supportsTemplating(Paths.get("style.css"))).isFalse();
    assertThat(supportsTemplating(Paths.get("style.less"))).isFalse();
    assertThat(supportsTemplating(Paths.get("text.zip"))).isFalse();
    assertThat(supportsTemplating(Paths.get("text.gz"))).isFalse();
    assertThat(supportsTemplating(Paths.get("text.pdf"))).isFalse();
    assertThat(supportsTemplating(Paths.get("image.gif"))).isFalse();
    assertThat(supportsTemplating(Paths.get("image.jpeg"))).isFalse();
    assertThat(supportsTemplating(Paths.get("image.jpg"))).isFalse();
    assertThat(supportsTemplating(Paths.get("image.png"))).isFalse();
    assertThat(supportsTemplating(Paths.get("font.svg"))).isFalse();
    assertThat(supportsTemplating(Paths.get("font.eot"))).isFalse();
    assertThat(supportsTemplating(Paths.get("font.ttf"))).isFalse();
    assertThat(supportsTemplating(Paths.get("font.woff"))).isFalse();
    assertThat(supportsTemplating(Paths.get("script.js"))).isFalse();
    assertThat(supportsTemplating(Paths.get("script.coffee"))).isFalse();
    assertThat(supportsTemplating(Paths.get("script.litcoffee"))).isFalse();
    assertThat(supportsTemplating(Paths.get("favicon.ico"))).isFalse();
    assertThat(supportsTemplating(Paths.get("unknown"))).isFalse();
  }
}
