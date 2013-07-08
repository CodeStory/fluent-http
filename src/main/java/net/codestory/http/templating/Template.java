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
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.io.*;

import com.github.mustachejava.*;

public class Template {
  private final Path path;

  public Template(String url) {
    this(Paths.get(url));
  }

  public Template(Path path) {
    this.path = path;
  }

  public String render() {
    return render(Collections.emptyMap());
  }

  public String render(String key, Object value) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(key, value);
    return render(keyValues);
  }

  public String render(String k1, String v1, String k2, Object v2) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    return render(keyValues);
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    keyValues.put(k3, v3);
    return render(keyValues);
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    keyValues.put(k3, v3);
    keyValues.put(k4, v4);
    return render(keyValues);
  }

  public String render(Map<String, Object> keyValues) {
    DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();

    try {
      String templateContent = Resources.read(path, StandardCharsets.UTF_8);

      ContentWithVariables parsedTemplate = new YamlFrontMatter().parse(templateContent);
      String content = parsedTemplate.getContent();
      Map<String, String> variables = parsedTemplate.getVariables();
      Map<String, Object> allKeyValues = merge(keyValues, variables);

      Mustache mustache = mustacheFactory.compile(new StringReader(content), "", "[[", "]]");

      Writer output = new StringWriter();
      mustache.execute(output, allKeyValues).flush();
      String body = output.toString();

      if (variables.containsKey("layout")) {
        String layoutName = (String) allKeyValues.get("layout");
        allKeyValues.put("body", "[[body]]");

        return new Template(path(layoutName)).render(allKeyValues).replace("[[body]]", body);
      }

      return body;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to render template", e);
    }
  }

  private static Map<String, Object> merge(Map<String, Object> keyValues, Map<String, String> variables) {
    Map<String, Object> merged = new HashMap<>();
    merged.putAll(keyValues);
    merged.putAll(variables);
    return merged;
  }

  private String path(String file) {
    return Resources.type(path) + ":" + file;
  }
}
