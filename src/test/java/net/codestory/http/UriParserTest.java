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
package net.codestory.http;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UriParserTest {
	@Test
	public void match_uri() {
		assertThat(new UriParser("/hello/:name").matches("/hello/Bob")).isTrue();
		assertThat(new UriParser("/hello/:name").matches("/helloBob")).isFalse();
		assertThat(new UriParser("/hello/:name").matches("/")).isFalse();
		assertThat(new UriParser("/").matches("/hello")).isFalse();
		assertThat(new UriParser("/:id/suffix").matches("/12/suffix")).isTrue();
		assertThat(new UriParser("/").matches("/")).isTrue();
		assertThat(new UriParser("/").matches("/no")).isFalse();
		assertThat(new UriParser("/no").matches("/")).isFalse();
	}

	@Test
	public void match_uri_with_query_params() {
		assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/Bob")).isTrue();
		assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/Bob?opt=OPTION")).isTrue();
		assertThat(new UriParser("/hello?name=:name").matches("/hello?name=Dave")).isTrue();
	}

	@Test
	public void find_params() {
		assertThat(new UriParser("/hello/:name").params("/hello/Bob")).containsExactly("Bob");
		assertThat(new UriParser("/hello/:name").params("/hello/Dave")).containsExactly("Dave");
		assertThat(new UriParser("/hello/:name/aged/:age").params("/hello/Dave/aged/42")).containsExactly("Dave", "42");
		assertThat(new UriParser("/hello/:name/aged/:age").params("/hello//aged/")).containsExactly("", "");
		assertThat(new UriParser("/").params("/")).isEmpty();
	}

	@Test
	public void find_query_params() {
		assertThat(new UriParser("/hello/:name?opt=:option").params("/hello/Bob?opt=OPTIONS")).containsExactly("Bob", "OPTIONS");
		assertThat(new UriParser("/hello/:name?opt=:option").params("/hello/Bob?opt=")).containsExactly("Bob", "");
		assertThat(new UriParser("/hello/:name?opt=:option&lang=:language").params("/hello/Bob?opt=OPTIONS&lang=FR")).containsExactly("Bob", "OPTIONS", "FR");
	}

	@Test
	public void params_count() {
		assertThat(UriParser.paramsCount("/hello")).isZero();
		assertThat(UriParser.paramsCount("/hello/:name")).isEqualTo(1);
		assertThat(UriParser.paramsCount("/hello/:name/:message")).isEqualTo(2);
		assertThat(UriParser.paramsCount("/hello/:name/:message?opt=:option&lang=:language")).isEqualTo(4);
	}

	@Test
	public void strip_query_params() {
		assertThat(UriParser.stripQueryParams("/hello")).isEqualTo("/hello");
		assertThat(UriParser.stripQueryParams("/hello?opt=:option")).isEqualTo("/hello");
	}

	@Test
	public void extract_query_params() {
		assertThat(UriParser.extractQueryParams("/hello")).isEmpty();
		assertThat(UriParser.extractQueryParams("/hello?")).isEmpty();
		assertThat(UriParser.extractQueryParams("/hello?opt=:option")).isEqualTo("opt=:option");
	}

	@Test
	public void dont_match_if_last_param_is_empty() {
		assertThat(new UriParser("/hello/:name").matches("/hello/")).isFalse();
		assertThat(new UriParser("/hello/:name/last").matches("/hello//last")).isTrue();
		assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/")).isFalse();
		assertThat(new UriParser("/hello/:name?opt=:option").matches("/hello/?opt=OPTION")).isFalse();
	}
}
