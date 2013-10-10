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
package net.codestory.http.io;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourcesTest {
	@Test
	public void exists() {
		assertThat(Resources.exists(Paths.get("index.html"))).isTrue();
		assertThat(Resources.exists(Paths.get("js"))).isFalse();
	}

	@Test
	public void list() {
		assertThat(Resources.list())
				.contains("js/script.coffee", "test.html")
				.doesNotContain("");
	}

	@Test
	public void ordered() {
		Set<String> list = Resources.list();
		Set<String> ordered = new TreeSet<>(list);

		assertThat(list).isEqualTo(ordered);
	}
}
