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

import static org.fest.assertions.Assertions.*;

import org.junit.*;

public class ContentTypesTest {
  ContentTypes contentTypes = new ContentTypes();

  @Test
  public void find_content_type_from_extension() {
    assertThat(contentTypes.get("index.html")).isEqualTo("text/html");
    assertThat(contentTypes.get("style.css")).isEqualTo("text/css");
    assertThat(contentTypes.get("style.less")).isEqualTo("text/css");
    assertThat(contentTypes.get("text.txt")).isEqualTo("text/plain");
    assertThat(contentTypes.get("text.zip")).isEqualTo("application/zip");
    assertThat(contentTypes.get("image.gif")).isEqualTo("image/gif");
    assertThat(contentTypes.get("image.jpeg")).isEqualTo("image/jpeg");
    assertThat(contentTypes.get("image.jpg")).isEqualTo("image/jpeg");
    assertThat(contentTypes.get("image.png")).isEqualTo("image/png");
    assertThat(contentTypes.get("script.js")).isEqualTo("application/javascript");
    assertThat(contentTypes.get("script.coffee")).isEqualTo("application/javascript");
    assertThat(contentTypes.get("unknown")).isEqualTo("text/plain");
  }

  @Test
  public void find_compatibily_with_templating() {
    assertThat(contentTypes.support_templating("index.html")).isTrue();
    assertThat(contentTypes.support_templating("style.css")).isTrue();
    assertThat(contentTypes.support_templating("style.less")).isTrue();
    assertThat(contentTypes.support_templating("text.txt")).isTrue();
    assertThat(contentTypes.support_templating("text.zip")).isFalse();
    assertThat(contentTypes.support_templating("image.gif")).isFalse();
    assertThat(contentTypes.support_templating("image.jpeg")).isFalse();
    assertThat(contentTypes.support_templating("image.jpg")).isFalse();
    assertThat(contentTypes.support_templating("image.png")).isFalse();
    assertThat(contentTypes.support_templating("script.js")).isTrue();
    assertThat(contentTypes.support_templating("script.coffee")).isTrue();
    assertThat(contentTypes.support_templating("unknown")).isFalse();
  }
}
