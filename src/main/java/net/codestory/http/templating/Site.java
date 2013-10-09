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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Site {
	private Map<String, Object> yaml;

	public Map<String, List<Map<String, Object>>> getTags() {
		Map<String, List<Map<String, Object>>> byTag = new HashMap<>();

		for (Map<String, Object> page : getPages()) {
			if (page.containsKey("tags")) {
				for (String tag : page.get("tags").toString().split(",")) {
					byTag.computeIfAbsent(tag.trim(), key -> new ArrayList<Map<String, Object>>()).add(page);
				}
			}
		}

		return byTag;
	}

	public Map<String, List<Map<String, Object>>> getCategories() {
		Map<String, List<Map<String, Object>>> byTag = new HashMap<>();

		for (Map<String, Object> page : getPages()) {
			if (page.containsKey("category")) {
				byTag.computeIfAbsent(page.get("category").toString().trim(), key -> new ArrayList<Map<String, Object>>()).add(page);
			}
		}

		return byTag;
	}

	public List<Map<String, Object>> getPages() {
		return Resources.list().stream().map(path -> {
			try {
				return YamlFrontMatter.parse(Resources.read(Paths.get(path), StandardCharsets.UTF_8)).getVariables();
			} catch (IOException e) {
				throw new IllegalStateException("Unable to read file: " + path, e);
			}
		}).collect(Collectors.toList());
	}

	Map<String, Object> configYaml() {
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
}
