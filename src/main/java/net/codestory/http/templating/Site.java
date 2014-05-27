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
import static java.nio.file.Files.*;
import static net.codestory.http.io.Resources.*;
import static net.codestory.http.io.FileVisitor.*;
import static net.codestory.http.misc.Fluent.*;
import static net.codestory.http.misc.MemoizingSupplier.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import net.codestory.http.convert.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;

import com.github.jknack.handlebars.*;

public class Site {
  private final Supplier<Set<String>> resourceList;
  private final Supplier<Map<String, Object>> yaml;
  private final Supplier<Map<String, Object>> data;
  private final Supplier<List<Map<String, Object>>> pages;
  private final Supplier<Map<String, List<Map<String, Object>>>> tags;
  private final Supplier<Map<String, List<Map<String, Object>>>> categories;

  public Site() {
    resourceList = memoize(() -> list(new Env()));

    yaml = memoize(() -> loadYamlConfig("_config.yml"));

    data = memoize(() -> of(resourceList.get())
      .filter(path -> path.startsWith("_data/"))
      .toMap(path -> nameWithoutExtension(path), path -> readYaml(path))
    );

    pages = memoize(() -> of(resourceList.get())
      .filter(path -> !path.startsWith("_"))
      .map(path -> Site.pathToMap(path))
      .toList()
    );

    tags = memoize(() -> {
      Map<String, List<Map<String, Object>>> tags = new TreeMap<>();
      for (Map<String, Object> page : getPages()) {
        for (String tag : tags(page)) {
          tags.computeIfAbsent(tag, key -> new ArrayList<Map<String, Object>>()).add(page);
        }
      }
      return tags;
    });

    categories = memoize(() -> of(getPages()).groupBy(page -> Site.category(page), TreeMap::new));
  }

  private static Set<String> list(Env env) {
    Set<String> paths = new TreeSet<>();

    Path rootPath = env.appPath();

    try {
      if (!env.disableClassPath()) {
        new ClasspathScanner().getResources(rootPath).forEach(resource -> paths.add(relativePath(rootPath, Paths.get(resource))));
      }

      if (!env.disableFilesystem()) {
        walkFileTree(rootPath, onFile(path -> paths.add(relativePath(rootPath, path))));
      }
    } catch (IOException e) {
      // Ignore
    }

    paths.remove("");

    return paths;
  }

  private Map<String, Object> configYaml() {
    return yaml.get();
  }

  public Object get(String key) {
    return yaml.get().get(key);
  }

  public <T> T getAs(String key, Class<T> type) {
    return TypeConvert.convertValue(get(key), type);
  }

  public Map<String, Object> getData() {
    return data.get();
  }

  public List<Map<String, Object>> getPages() {
    return pages.get();
  }

  public Map<String, List<Map<String, Object>>> getTags() {
    return tags.get();
  }

  public Map<String, List<Map<String, Object>>> getCategories() {
    return categories.get();
  }

  private static Map<String, Object> pathToMap(String path) {
    try {
      return YamlFrontMatter.parse(Paths.get(path)).getVariables();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read file: " + path, e);
    }
  }

  private static Object readYaml(String path) {
    try {
      return YamlParser.INSTANCE.parse(Resources.read(Paths.get(path), UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read file: " + path, e);
    }
  }

  private static String category(Map<String, Object> page) {
    return page.getOrDefault("category", "").toString().trim();
  }

  private static String[] tags(Map<String, Object> page) {
    Object tags = page.getOrDefault("tags", "");
    if (tags instanceof List) {
      return ((List<String>) tags).toArray(new String[0]);
    }
    return tags.toString().trim().split("\\s*,\\s*");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> loadYamlConfig(String configFile) {
    Path configPath = Paths.get(configFile);
    if (!Resources.exists(configPath)) {
      return new HashMap<>();
    }

    try {
      return YamlParser.INSTANCE.parseMap(Resources.read(configPath, UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read " + configFile, e);
    }
  }

  private static String nameWithoutExtension(String path) {
    return Strings.substringBeforeLast(Paths.get(path).getFileName().toString(), ".");
  }

  static enum SiteValueResolver implements ValueResolver {
    INSTANCE;

    @Override
    public Object resolve(Object context, String name) {
      if (context instanceof Site) {
        return ((Site) context).configYaml().getOrDefault(name, UNRESOLVED);
      }
      return UNRESOLVED;
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
