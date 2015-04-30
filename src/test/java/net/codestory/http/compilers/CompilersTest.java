/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.http.compilers;

import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.io.Resources;
import net.codestory.http.misc.*;

import org.junit.*;

public class CompilersTest {
  static Env env = Env.prod();
  static Resources resources = new Resources(env);
  static Compilers compilers = new Compilers(env, resources);

  private String compile(String filename, String content) {
    return compilers.compile(new SourceFile(Paths.get(filename), content)).content();
  }

  @Test(expected = IllegalArgumentException.class)
  public void do_not_compile_plain_file() {
    compile("plain.txt", "");
  }

  @Test
  public void register_custom_compiler() {
    compilers.register(() -> (sourceFile) -> sourceFile.getSource() + sourceFile.getSource(), ".html", ".copycat");

    String source = compile("file.copycat", "Hello");

    assertThat(source).isEqualTo("HelloHello");
  }

  @Test
  public void supports_file_cache_being_destroyed() {
    // Delete cache
    File cacheFile = Paths.get(System.getProperty("user.home"), ".code-story", "cache", "V11", "prod", "coffee", "a74af0f5c2a722faf152cf330ae29c43fc689123").toFile();
    cacheFile.delete();

    // Fill cache
    String javascript = compile("test.coffee", "a=42");

    assertThat(cacheFile).exists();
    assertThat(javascript).contains("var a;\n\na = 42");

    // Delete cache
    cacheFile.delete();
    String updated = compile("test.coffee", "a=1337");

    assertThat(updated).contains("var a;\n\na = 1337");
  }

  @Test
  public void compare_cached_coffee_version_with_compiled_version() {
    String source = "a=42";

    String cached = compile("source.coffee", source);
    String compiled = new CoffeeCompiler(true).compile(new SourceFile(Paths.get("source.coffee"), source));

    assertThat(cached).isEqualTo(compiled);
  }

  @Test
  public void compare_cached_less_version_with_compiled_version() {
    String source = "body { h1 { color: red; } }";

    String cached = compile("source.less", source);
    String compiled = new LessCompiler(resources, true).compile(new SourceFile(Paths.get("source.less"), source));

    assertThat(cached).isEqualTo(compiled);
  }
}
