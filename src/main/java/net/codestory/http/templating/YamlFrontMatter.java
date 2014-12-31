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
package net.codestory.http.templating;

import static java.nio.charset.StandardCharsets.*;
import static java.util.regex.Pattern.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import net.codestory.http.io.*;

public class YamlFrontMatter {
  private static final Pattern FRONT_MATTER = compile("\\A\\s*(?:---\\r?\\n((?:(?!---).)*)---\\s*\\r?\\n)?(.*)\\z", DOTALL);

  private final Path path;
  private final String content;
  private final Map<String, Object> variables;

  private YamlFrontMatter(Path path, String content, Map<String, Object> variables) {
    this.path = path;
    this.content = content;
    this.variables = new HashMap<>(variables);
    this.variables.put("content", content);
    this.variables.put("path", path);
    this.variables.put("name", Strings.substringBeforeLast(path.getFileName().toString(), "."));
  }

  public Path getPath() {
    return path;
  }

  public String getContent() {
    return content;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  // TODO: remove reference to Resources
  public static YamlFrontMatter parse(Resources resources, Path path) throws IOException {
    return parse(path, resources.read(path, UTF_8));
  }

  public static YamlFrontMatter parse(Path path, String text) {
    Matcher matcher = FRONT_MATTER.matcher(text);

    boolean matches = matcher.matches();
    String header = matches ? matcher.group(1) : null;
    String content = matches ? matcher.group(2) : text;

    return new YamlFrontMatter(path, content, parseVariables(header));
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> parseVariables(String header) {
    if (header == null) {
      return Collections.emptyMap();
    }
    return YamlParser.INSTANCE.parseMap(header);
  }
}
