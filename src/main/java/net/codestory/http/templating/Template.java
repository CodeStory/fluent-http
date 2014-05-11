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
package net.codestory.http.templating;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.*;

public class Template {
  private final Path path;

  public Template(String uri) {
    this(uri, Resources.findExistingPath(uri));
  }

  private Template(String folder, String name) {
    this(folder + "/" + name, Resources.findExistingPath(folder, name));
  }

  private Template(String uri, Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Template not found " + uri);
    }
    this.path = path;
  }

  public CacheEntry render(Map<String, ?> keyValues) {
    try {
      YamlFrontMatter yamlFrontMatter = YamlFrontMatter.parse(path);

      String content = yamlFrontMatter.getContent();
      Map<String, Object> variables = yamlFrontMatter.getVariables();
      Map<String, Object> allKeyValues = merge(variables, keyValues);

      String body = HandlebarsCompiler.INSTANCE.compile(content, allKeyValues);
      CacheEntry entry = Compilers.INSTANCE.compile(path, body);

      String layout = (String) variables.get("layout");
      if (layout == null) {
        return entry;
      }

      String layoutContent = new Template("_layouts", layout).render(allKeyValues).content();
      String bodyWithLayout = layoutContent.replace("[[body]]", entry.content());

      return CacheEntry.fromString(bodyWithLayout); // Could have a more aggressive lastModified()
    } catch (IOException e) {
      throw new IllegalStateException("Unable to render template", e);
    }
  }

  private static Map<String, Object> merge(Map<String, ?> first, Map<String, ?> second) {
    Map<String, Object> merged = new HashMap<>();
    merged.putAll(first);
    merged.putAll(second);
    merged.put("body", "[[body]]");
    return merged;
  }
}
