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

import java.io.*;
import java.net.*;
import java.nio.file.*;

import net.codestory.http.io.*;
import org.junit.*;

public class CompilersTest {
  @After
  public void tearDown() throws Exception {
    //Remove file created in test compile_less_file_with_import
    URL rootURL = Resources.getResource("private.txt");
    Path otherPath = Paths.get(new File(rootURL.getFile()).getParent(), "app/assets/other.less");
    Files.deleteIfExists(otherPath);
  }

  @Test
  public void compile_less_file() {
    String css = Compilers.INSTANCE.compile(Paths.get("style.less"), "body { h1 { color: red; } }").content();

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=style.less.map */");
  }

  @Test
  @Ignore("To fix Compilers problem with cache.computeIfAbsent")
  public void compile_less_file_with_import() throws IOException {
    copy("app/assets/style.less", "app/assets/other.less");
    String css = Compilers.INSTANCE.compile(Paths.get("styleWithImport.less"), "@import '/assets/other.less';").content();
    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=styleWithImport.css.map */\n");

    copy("app/assets/style.css", "app/assets/other.less");
    css = Compilers.INSTANCE.compile(Paths.get("styleWithImport.less"), "@import '/assets/other.less';").content();
    assertThat(css).isEqualTo("* {}\n/*# sourceMappingURL=styleWithImport.css.map */\n");
  }

  private void copy(String source, String target) throws IOException {
    URL rootURL = Resources.getResource("private.txt");
    InputStream sourceStream = Resources.getResourceAsStream(source);
    Path otherPath = Paths.get(new File(rootURL.getFile()).getParent(), target);
    Files.copy(sourceStream, otherPath, StandardCopyOption.REPLACE_EXISTING);
  }

  @Test
  public void do_not_compile_plain_file() {
    String css = Compilers.INSTANCE.compile(Paths.get("plain.txt"), "Hello").content();

    assertThat(css).isEqualTo("Hello");
  }

  @Test
  public void register_custom_compiler() {
    Compilers.INSTANCE.register(() -> (path, source) -> source + source, ".copycat");

    String source = Compilers.INSTANCE.compile(Paths.get("file.copycat"), "Hello").content();

    assertThat(source).isEqualTo("HelloHello");
  }

  @Test
  public void supports_file_cache_being_destroyed() {
    // Delete cache
    File cacheFile = Paths.get(System.getProperty("user.home"), ".code-story", "cache", "V2", "less", "a4c0dac49e47ffe0dbcca7615f73b72ef6b71543").toFile();
    cacheFile.delete();

    // Fill cache
    Compilers.INSTANCE.compile(Paths.get("body.less"), "body{}").content();
    assertThat(cacheFile).exists();

    // Delete cache
    cacheFile.delete();
    String css = Compilers.INSTANCE.compile(Paths.get("body.less"), "body{}").content();

    assertThat(css).isEqualTo("/*# sourceMappingURL=body.less.map */");
  }
}
