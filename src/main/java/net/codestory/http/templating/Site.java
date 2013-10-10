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
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Site {
	private Map<String, Object> yaml;
	private Map<String, List<Map<String, Object>>> tags;
	private Map<String, List<Map<String, Object>>> categories;
	private List<Map<String, Object>> pages;

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
