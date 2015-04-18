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
package net.codestory.http.markdown;

import static java.util.Arrays.*;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.*;

import org.junit.*;

import java.util.List;
import java.util.Map;

public class TablePluginTest {
  static TablePlugin plugin = new TablePlugin();

  private String applyPlugin(List<String> lines, Map<String, String> parameters) {
    StringBuilder html = new StringBuilder();
    plugin.emit(html, lines, parameters);
    return html.toString();
  }

  @Test
  public void empty() {
    String html = applyPlugin(asList(""), emptyMap());

    assertThat(html).isEqualTo("<table>\n</table>\n");
  }

  @Test
  public void header() {
    String html = applyPlugin(asList("H1|H2|H3"), emptyMap());

    assertThat(html).isEqualTo("<table>\n" +
      "<tr><th>H1</th><th>H2</th><th>H3</th></tr>\n" +
      "</table>\n");
  }

  @Test
  public void rows() {
    String html = applyPlugin(asList("H1|H2|H3", "A1|A2|A3", "B1|B2|B3"), emptyMap());

    assertThat(html).isEqualTo("<table>\n" +
      "<tr><th>H1</th><th>H2</th><th>H3</th></tr>\n" +
      "<tr><td>A1</td><td>A2</td><td>A3</td></tr>\n" +
      "<tr><td>B1</td><td>B2</td><td>B3</td></tr>\n" +
      "</table>\n");
  }

  @Test
  public void id() {
    String html = applyPlugin(asList(""), singletonMap("id", "AN_ID"));

    assertThat(html).isEqualTo("<table id=\"AN_ID\">\n</table>\n");
  }
}
