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

import com.jayway.restassured.specification.ResponseSpecification;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Post;
import net.codestory.http.templating.Template;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.jayway.restassured.RestAssured.given;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.spy;

public class WebServerTest {
	static WebServer server = new WebServer() {
		@Override
		protected boolean devMode() {
			return false;
		}
	};

	@BeforeClass
	public static void startServer() {
		server.startOnRandomPort();
	}

	@Before
	public void resetWebServer() {
		server.reset();
	}

	@Test
	public void not_found() {
		expect().content(containsString("Page not found")).contentType("text/html").statusCode(404).when().get("/notfound");
	}

	@Test
	public void content_types() {
		server.configure(routes -> {
			routes.get("/index", () -> "Hello");
			routes.get("/raw", () -> "RAW DATA".getBytes(UTF_8));
			routes.get("/json", () -> new Person("NAME", 42));
			routes.get("/text", () -> new Payload("text/plain", "TEXT"));
			routes.get("/otherText", new Payload("text/plain", "OTHER"));
		});

		expect().body(equalTo("Hello")).contentType("text/html").when().get("/index");
		expect().body(equalTo("RAW DATA")).contentType("application/octet-stream").when().get("/raw");
		expect().body("name", equalTo("NAME")).body("age", equalTo(42)).contentType("application/json").when().get("/json");
		expect().body(equalTo("TEXT")).contentType("text/plain").when().get("/text");
		expect().body(equalTo("TEXT")).contentType("text/plain").when().get("/text");
		expect().body(equalTo("OTHER")).contentType("text/plain").when().get("/otherText");
	}

	@Test
	public void request_params() {
		server.configure(routes -> {
			routes.get("/hello/:name", (name) -> "Hello " + name);
			routes.get("/other/:name", (name) -> "Other " + name);
			routes.get("/say/:what/how/:loud", (what, loud) -> what + " " + loud);
			routes.get("/:one/:two/:three", (one, two, three) -> one + two + three);
		});

		expect().body(equalTo("Hello Dave")).when().get("/hello/Dave");
		expect().body(equalTo("Hello Bob")).when().get("/hello/Bob");
		expect().body(equalTo("Hello John Doe")).when().get("/hello/John Doe");
		expect().body(equalTo("Other Joe")).when().get("/other/Joe");
		expect().body(equalTo("HI LOUD")).when().get("/say/HI/how/LOUD");
	}

	@Test
	public void static_content_from_classpath() {
		expect().content(containsString("Hello From a File")).contentType("text/html").when().get("/index.html");
		expect().content(containsString("Hello From a File")).contentType("text/html").when().get("/");
		expect().content(containsString("TEST")).contentType("text/html").when().get("/test.html");
		expect().content(containsString("TEST")).contentType("text/html").when().get("/test");
		expect().content(containsString("console.log('Hello');")).contentType("application/javascript").when().get("/js/script.js");
		expect().content(containsString("console.log('Hello');")).contentType("application/javascript").when().get("/js/script.coffee");
		expect().content(containsString("* {}")).contentType("text/css").when().get("/assets/style.css");
		expect().content(containsString("body h1 {\n  color: red;\n}\n")).contentType("text/css").when().get("/assets/style.less");
		expect().content(containsString("<strong>Hello</strong>")).contentType("text/html").when().get("/hello.md");
		expect().content(containsString("<strong>Good Bye</strong>")).contentType("text/html").when().get("/goodbye.markdown");
		expect().statusCode(404).when().get("/../private.txt");
		expect().statusCode(404).when().get("/_config.yaml");
		expect().statusCode(404).when().get("/_layouts/default.html");
		expect().statusCode(404).when().get("/unknown");
	}

	@Test
	public void dont_serve_directories() {
		expect().statusCode(404).when().get("/js");
	}

	@Test
	public void annotated_resources() {
		server.configure(routes -> routes.add(new Object() {
			@Get("/hello")
			@Get("/")
			public String hello() {
				return "Hello";
			}

			@Get("/bye/:whom")
			public String bye_to_(String whom) {
				return "Good Bye " + whom;
			}

			@Get("/add/:left/:right")
			public String bye_to_(int left, int right) {
				return Integer.toString(left + right);
			}
		}));

		expect().content(equalTo("Hello")).when().get("/hello");
		expect().content(equalTo("Hello")).when().get("/");
		expect().content(equalTo("Good Bye Bob")).when().get("/bye/Bob");
		expect().content(equalTo("42")).when().get("/add/22/20");
	}

	@Test
	public void annotated_resources_with_prefix() {
		server.configure(routes -> routes.add("/say", new Object() {
			@Get("/hello")
			public String say_hello() {
				return "Hello";
			}
		}));

		expect().content(equalTo("Hello")).when().get("/say/hello");
	}

	@Test
	public void ignore_query_params() {
		server.configure(routes -> routes.get("/index", () -> "Hello"));

		expect().content(equalTo("Hello")).when().get("/index?query=value");
	}

	@Test
	public void streams() {
		server.configure(routes -> routes.get("/", () -> new Payload("text/html", new ByteArrayInputStream("Hello".getBytes()))));

		expect().content(equalTo("Hello")).contentType("text/html").when().get("/");
	}

	@Test
	public void templates() {
		server.configure(routes -> routes.get("/hello/:name", (name) -> new Template("1variable.txt").render("name", name)));

		expect().content(containsString("<div>_PREFIX_TEXT_SUFFIX_</div>")).when().get("/pageYaml");
		expect().content(equalTo("Hello Joe")).when().get("/hello/Joe");
	}

	@Test
	public void priority_to_route() {
		server.configure(routes -> routes.get("/", () -> "PRIORITY"));

		expect().content(equalTo("PRIORITY")).when().get("/");
	}

	@Test
	public void redirect() {
		server.configure(routes -> {
			routes.get("/index", () -> Payload.seeOther("/login"));
			routes.get("/login", () -> "LOGIN");
		});

		expect().content(equalTo("LOGIN")).when().get("/index");
	}

	@Test
	public void filter() {
		server.configure(routes -> {
			routes.get("/", () -> "NOT FILTERED");
			routes.get("/other", () -> "OTHER");
			routes.filter((uri, exchange) -> {
				if ("/".equals(uri)) {
					exchange.getResponseHeaders().add("Content-Type", "text/html");
					exchange.sendResponseHeaders(200, 8);
					exchange.getResponseBody().write("FILTERED".getBytes());
					return true;
				}
				return false;
			});
		});

		expect().content(equalTo("FILTERED")).when().get("/");
		expect().content(equalTo("OTHER")).when().get("/other");
	}

	@Test
	public void error() {
		server.configure(routes -> routes.get("/", () -> {
			throw new RuntimeException("BUG");
		}));

		expect().content(containsString("An error occurred on the server")).contentType("text/html").statusCode(500).when().get("/");
	}

	@Test
	public void supports_spied_resources() {
		server.configure(routes -> routes.add(spy(new TestResource())));

		expect().content(equalTo("HELLO")).when().get("/");
	}

	@Test
	public void post() {
		server.configure(routes -> {
			routes.post("/post", () -> "Done");
			routes.get("/get", () -> "Done");
			routes.get("/action", () -> "Done GET");
			routes.post("/action", () -> "Done POST");
			routes.post("/post/:who", (who) -> "Done " + who);
			routes.add(new Object() {
				@Post("/person")
				@Post("/person_alt")
				public String created() {
					return "CREATED";
				}
			});
		});

		expect().content(equalTo("Done")).when().post("/post");
		expect().content(equalTo("Done Bob")).when().post("/post/Bob");
		expect().content(equalTo("Done POST")).when().post("/action");
		expect().content(equalTo("Done GET")).when().get("/action");
		expect().content(equalTo("CREATED")).when().post("/person");
		expect().content(equalTo("CREATED")).when().post("/person_alt");
		expect().statusCode(405).when().post("/get");
		expect().statusCode(405).when().post("/index.html");
		expect().statusCode(404).when().post("/unknown");
	}

	@Test
	public void site_variables() {
		expect().content(containsString("<p>\njava, scala\n\nscala\n</p>\n<p>\nscala\n</p>")).when().get("/testTags");
	}

	static class TestResource {
		@Get("/")
		public String hello() {
			return "HELLO";
		}
	}

	private ResponseSpecification expect() {
		return given().port(server.port()).expect();
	}

	static class Person {
		final String name;
		final int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
}
