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

import java.util.*;

import net.codestory.http.compilers.SourceFile;
import org.yaml.snakeyaml.*;

import static java.util.Collections.emptyMap;

public enum YamlParser {
  INSTANCE;

  @SuppressWarnings("unchecked")
  public Map<String, Object> parseMap(String content) {
    Map<String, Object> variables = (Map<String, Object>) createYaml().load(content);

    return variables != null ? variables : emptyMap();
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> parseMap(SourceFile sourceFile) {
    return parseMap(sourceFile.getSource());
  }

  @SuppressWarnings("unchecked")
  public Object parse(SourceFile sourceFile) {
    return createYaml().load(sourceFile.getSource());
  }

  // Not thread-safe
  private Yaml createYaml() {
    return new Yaml();
  }
}
