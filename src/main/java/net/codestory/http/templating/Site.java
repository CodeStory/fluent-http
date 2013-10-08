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

import net.codestory.http.io.Resources;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Site {
  private Map<String, Object> values;

  public Map<String, Object> getSite() {
    if (values == null) {
      values = new HashMap<>();

      values.put("name", "toto");
      values.putAll(loadYamlConfig("_config.yml"));
    }
    return values;
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
}
