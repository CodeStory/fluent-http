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
package net.codestory.http.misc;

public class UrlConcat {
  public String url(String resourcePrefix, String classPrefix, String uri) {
    StringBuilder urlBuilder = new StringBuilder();

    appendUrl(urlBuilder, resourcePrefix);
    appendUrl(urlBuilder, classPrefix);
    appendUrl(urlBuilder, uri);

    return urlBuilder.toString();
  }

  private static void appendUrl(StringBuilder appendTo, String path) {
    if (path.isEmpty()) {
      return;
    }

    if ((path.charAt(0) == '/') && endWith(appendTo, '/')) {
      appendTo.append(path, 1, path.length());
    } else if ((path.charAt(0) != '/') && !endWith(appendTo, '/')) {
      appendTo.append('/').append(path);
    } else {
      appendTo.append(path);
    }
  }

  private static boolean endWith(StringBuilder appendTo, char character) {
    int length = appendTo.length();
    return (length > 0) && (appendTo.charAt(length - 1) == character);
  }
}
