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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class HandlebarsCompilerTest {
	HandlebarsCompiler compiler = new HandlebarsCompiler();

	@Test
	public void compile() throws IOException {
		String result = compiler.compile("-[[greeting]]-", map("greeting", "Hello"));

		assertThat(result).isEqualTo("-Hello-");
	}

	@Test
	public void partials() throws IOException {
		String result = compiler.compile("-[[>partial]] [[>partial]]-", map("name", "Bob"));

		assertThat(result).isEqualTo("-Hello Bob Hello Bob-");
	}

	@Test
	public void string_helpers() throws IOException {
		String result = compiler.compile("Hello [[capitalizeFirst name]]", map("name", "joe"));

		assertThat(result).isEqualTo("Hello Joe");
	}

	@Test
	public void java_getters_and_fields() throws IOException {
		String result = compiler.compile("[[bean.name]] is [[bean.age]]", map("bean", new JavaBean("Bob", 12)));

		assertThat(result).isEqualTo("Bob is 12");
	}

	@Test
	public void each() throws IOException {
		String result = compiler.compile("[[#each list]][[.]][[/each]]", map("list", asList("A", "B")));

		assertThat(result).isEqualTo("AB");
	}

	@Test
	public void each_reverse() throws IOException {
		String result = compiler.compile("[[#each_reverse list]][[.]][[/each_reverse]]", map("list", asList("A", "B")));

		assertThat(result).isEqualTo("BA");
	}

	private static Map<String, Object> map(String key, Object value) {
		return new HashMap<String, Object>() {{
			put(key, value);
		}};
	}

	public static class JavaBean {
		private final String name;
		public final int age;

		private JavaBean(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}
	}
}
