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

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import net.codestory.http.io.*;

import com.github.jknack.handlebars.*;

public class Site {
  private static Site INSTANCE = new Site();

  private Map<String, Object> yaml;
  private Map<String, List<Map<String, Object>>> tags;
  private Map<String, List<Map<String, Object>>> categories;
  private List<Map<String, Object>> pages;

  private Site() {
    // Private constructor
  }

  public static Site get() {
    if (Boolean.getBoolean("PROD_MODE")) {
      return INSTANCE;
    }

    return new Site();
  }

  public Map<String, List<Map<String, Object>>> getTags() {
    if (tags == null) {
      tags = new TreeMap<>();

      for (Map<String, Object> page : getPages()) {
        for (String tag : tags(page)) {
          tags.computeIfAbsent(tag, key -> new ArrayList<Map<String, Object>>()).add(page);
        }
      }
    }

    return tags;
  }

  public Map<String, List<Map<String, Object>>> getCategories() {
    if (categories == null) {
      Map<String, List<Map<String, Object>>> notSorted = getPages().stream().collect(Collectors.groupingBy(Site::category));
      categories = new TreeMap<>(notSorted);
    }
    return categories;
  }

  public List<Map<String, Object>> getPages() {
    if (pages == null) {
      pages = Resources.list().stream().map(Site::pathToMap).collect(Collectors.toList());
    }
    return pages;
  }

  private static Map<String, Object> pathToMap(String path) {
    try {
      return YamlFrontMatter.parse(Paths.get(path)).getVariables();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read file: " + path, e);
    }
  }

  private static String category(Map<String, Object> page) {
    return page.getOrDefault("category", "").toString().trim();
  }

  private static String[] tags(Map<String, Object> page) {
    return page.getOrDefault("tags", "").toString().trim().split("\\s*,\\s*");
  }

  public Object get(String key) {
    return configYaml().get(key);
  }

  private Map<String, Object> configYaml() {
    if (yaml == null) {
      yaml = loadYamlConfig("_config.yml");
    }
    return yaml;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> loadYamlConfig(String configFile) {
    Path configPath = Paths.get(configFile);
    if (!Resources.exists(configPath)) {
      return new HashMap<>();
    }

    try {
      return new YamlParser().parse(Resources.read(configPath, UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read " + configFile, e);
    }
  }

  static enum SiteValueResolver implements ValueResolver {
    INSTANCE;

    @Override
    public Object resolve(Object context, String name) {
      Object value = null;
      if (context instanceof Site) {
        value = ((Site) context).configYaml().get(name);
      }
      return value == null ? UNRESOLVED : value;
    }

    @Override
    public Set<Map.Entry<String, Object>> propertySet(Object context) {
      if (context instanceof Site) {
        return ((Site) context).configYaml().entrySet();
      }
      return Collections.emptySet();
    }
  }
}
