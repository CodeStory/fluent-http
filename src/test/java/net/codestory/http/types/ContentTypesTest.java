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

import org.junit.*;

public class ContentTypesTest {
  @Test
  public void content_type_from_extension() {
    assertThat(get("index.html")).isEqualTo("text/html;charset=UTF-8");
    assertThat(get("file.json")).isEqualTo("application/json;charset=UTF-8");
    assertThat(get("data.xml")).isEqualTo("application/xml;charset=UTF-8");
    assertThat(get("style.css")).isEqualTo("text/css;charset=UTF-8");
    assertThat(get("style.less")).isEqualTo("text/css;charset=UTF-8");
    assertThat(get("style.css.map")).isEqualTo("text/plain;charset=UTF-8");
    assertThat(get("text.md")).isEqualTo("text/html;charset=UTF-8");
    assertThat(get("text.markdown")).isEqualTo("text/html;charset=UTF-8");
    assertThat(get("text.txt")).isEqualTo("text/plain;charset=UTF-8");
    assertThat(get("text.zip")).isEqualTo("application/zip");
    assertThat(get("text.gz")).isEqualTo("application/gzip");
    assertThat(get("text.pdf")).isEqualTo("application/pdf");
    assertThat(get("image.gif")).isEqualTo("image/gif");
    assertThat(get("image.jpeg")).isEqualTo("image/jpeg");
    assertThat(get("image.jpg")).isEqualTo("image/jpeg");
    assertThat(get("image.png")).isEqualTo("image/png");
    assertThat(get("font.svg")).isEqualTo("image/svg+xml");
    assertThat(get("font.eot")).isEqualTo("application/vnd.ms-fontobject");
    assertThat(get("font.ttf")).isEqualTo("application/x-font-ttf");
    assertThat(get("font.woff")).isEqualTo("application/x-font-woff");
    assertThat(get("script.js")).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get("script.coffee")).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get("script.litcoffee")).isEqualTo("application/javascript;charset=UTF-8");
    assertThat(get("favicon.ico")).isEqualTo("image/x-icon");
    assertThat(get("unknown")).isEqualTo("text/plain;charset=UTF-8");
  }
}
