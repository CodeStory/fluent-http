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
package net.codestory.http.compilers.markdown;

import static java.util.Arrays.*;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.*;

import org.junit.*;

import java.util.List;
import java.util.Map;

public class FormulaPluginTest {
  static FormulaPlugin plugin = new FormulaPlugin();

  private String applyPlugin(List<String> lines, Map<String, String> parameters) {
    StringBuilder html = new StringBuilder();
    plugin.emit(html, lines, parameters);
    return html.toString();
  }

  @Test
  public void to_formula_url() {
    String html = applyPlugin(asList("1+2"), emptyMap());

    assertThat(html).isEqualTo("<img src=\"http://latex.codecogs.com/png.download?1%2B2\" />");
  }

  @Test
  public void to_gif() {
    String html = applyPlugin(asList("2+3"), singletonMap("type", "gif"));

    assertThat(html).isEqualTo("<img src=\"http://latex.codecogs.com/gif.download?2%2B3\" />");
  }

  @Test
  public void skip_blank_lines() {
    String html = applyPlugin(asList(" ", "2+3", ""), emptyMap());

    assertThat(html).isEqualTo("<img src=\"http://latex.codecogs.com/png.download?2%2B3\" />");
  }

  @Test
  public void dont_replace_spaces_with_plus_sign() {
    String html = applyPlugin(asList(" ", "a b", ""), emptyMap());

    assertThat(html).isEqualTo("<img src=\"http://latex.codecogs.com/png.download?a%20b\" />");
  }

  @Test
  public void encode_url() {
    String encoded = FormulaPlugin.encode("https://www.google.fr/1 2");

    assertThat(encoded).isEqualTo("https%3A%2F%2Fwww.google.fr%2F1%202");
  }
}
