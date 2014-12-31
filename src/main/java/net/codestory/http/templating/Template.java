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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.markdown.MarkdownCompiler;

public class Template {
  private final Resources resources;
  private final Path path;

  public Template(Resources resources, String uri) {
    this(resources, uri, resources.findExistingPath(uri));
  }

  private Template(Resources resources, String folder, String name) {
    this(resources, folder + "/" + name, resources.findExistingPath(folder, name));
  }

  private Template(Resources resources, String uri, Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Template not found " + uri);
    }
    this.resources = resources;
    this.path = path;
  }

  public String render(Map<String, ?> keyValues, CompilerFacade compilerFacade) {
    try {
      YamlFrontMatter yamlFrontMatter = YamlFrontMatter.parse(resources.sourceFile(path));

      String content = yamlFrontMatter.getContent();
      Map<String, Object> variables = yamlFrontMatter.getVariables();
      Map<String, Object> allKeyValues = merge(variables, keyValues);

      String body = compilerFacade.handlebar(content, allKeyValues);
      if (MarkdownCompiler.supports(path)) {
        body = MarkdownCompiler.INSTANCE.compile(body);
      }

      String layout = (String) variables.get("layout");
      if (layout == null) {
        return body;
      }

      String layoutContent = new Template(resources, "_layouts", layout).render(allKeyValues, compilerFacade);
      String bodyWithLayout = layoutContent.replace("[[body]]", body);
      return bodyWithLayout;
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
