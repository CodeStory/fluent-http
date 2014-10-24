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
package net.codestory.http.compilers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.misc.*;

import org.junit.*;

public class CompilersTest {
  private static Compilers compilers = new Compilers(prodMode());

  @Test
  public void do_not_compile_plain_file() {
    String css = compilers.compile(Paths.get("plain.txt"), "Hello").content();

    assertThat(css).isEqualTo("Hello");
  }

  @Test
  public void register_custom_compiler() {
    compilers.register(() -> (path, source) -> source + source, ".html", ".copycat");

    String source = compilers.compile(Paths.get("file.copycat"), "Hello").content();

    assertThat(source).isEqualTo("HelloHello");
  }

  @Test
  public void supports_file_cache_being_destroyed() {
    // Delete cache
    File cacheFile = Paths.get(System.getProperty("user.home"), ".code-story", "cache", "V3", "prod", "coffee", "469d8cd9668f810e3a9984472792076cae0e1883").toFile();
    cacheFile.delete();

    // Fill cache
    String javascript = compilers.compile(Paths.get("test.coffee"), "a=42").content();

    assertThat(cacheFile).exists();
    assertThat(javascript).contains("var a;\n\na = 42");

    // Delete cache
    cacheFile.delete();
    String updated = compilers.compile(Paths.get("test.coffee"), "a=1337").content();

    assertThat(updated).contains("var a;\n\na = 1337");
  }

  private static Env prodMode() {
    Env env = mock(Env.class);
    when(env.prodMode()).thenReturn(true);
    return env;
  }
}
