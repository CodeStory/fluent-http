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

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlFrontMatterTest {
	@Test
	public void should_read_empty_file() {
		String content = content("");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getContent()).isEmpty();
		assertThat(parsed.getVariables()).hasSize(1).containsEntry("content", "");
	}

	@Test
	public void should_read_file_without_headers() {
		String content = content("CONTENT");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getContent()).isEqualTo("CONTENT");
		assertThat(parsed.getVariables()).hasSize(1).containsEntry("content", "CONTENT");
	}

	@Test
	public void should_read_header_variables() {
		String content = content(
				"---",
				"layout: standard",
				"title: CodeStory - Devoxx Fight",
				"---",
				"BODY");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getContent()).isEqualTo("BODY");
		assertThat(parsed.getVariables())
				.containsEntry("content", "BODY")
				.containsEntry("layout", "standard")
				.containsEntry("title", "CodeStory - Devoxx Fight");
	}

	@Test
	public void should_ignore_commented_variable() {
		String content = content(
				"---",
				"#layout: standard",
				"title: CodeStory - Devoxx Fight",
				"---",
				"CONTENT");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getVariables())
				.doesNotContainEntry("layout", "standard")
				.doesNotContainEntry("#layout", "standard")
				.containsEntry("title", "CodeStory - Devoxx Fight");
	}

	@Test
	public void escape_strings_with_quotes() {
		String content = content(
				"---",
				"title: \'{{Code}} Fight by Code-Story\'",
				"---",
				"CONTENT");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getVariables())
				.containsEntry("title", "{{Code}} Fight by Code-Story");
	}

	@Test
	public void complex_yaml() {
		String content = content(
				"---",
				"products: ",
				" - name: PROD1",
				" - name: PROD2",
				"---",
				"CONTENT");

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> products = (List<Map<String, Object>>) parsed.getVariables().get("products");
		assertThat(products).hasSize(2);
		assertThat(products.get(0)).containsEntry("name", "PROD1");
		assertThat(products.get(1)).containsEntry("name", "PROD2");
	}

	@Test
	public void ignore_dashes_in_content() {
		String content = content(
				"---",
				"title: TITLE",
				"---",
				"START",
				"---",
				"END"
		);

		YamlFrontMatter parsed = YamlFrontMatter.parse(content);

		assertThat(parsed.getContent()).isEqualTo("START\n---\nEND");
		assertThat(parsed.getVariables()).containsEntry("title", "TITLE");
	}

	static String content(String... lines) {
		return String.join("\n", lines);
	}
}
