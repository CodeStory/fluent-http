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

import java.util.*;

import org.markdown4j.*;

class TablePlugin extends Plugin {
  public TablePlugin() {
    super("table");
  }

  @Override
  public void emit(StringBuilder result, List<String> lines, Map<String, String> parameters) {
    String id = parameters.get("id");
    if (id == null) {
      result.append("<table>\n");
    } else {
      result.append(String.format("<table id=\"%s\">\n", id));
    }

    boolean header = true;

    for (String line : lines) {
      if (!line.trim().isEmpty()) {
        result.append("<tr>");

        for (String value : line.split("[|]")) {
          result.append(header ? "<th>" : "<td>");
          result.append(value);
          result.append(header ? "</th>" : "</td>");
        }

        header = false;

        result.append("</tr>\n");
      }
    }

    result.append("</table>\n");
  }
}
