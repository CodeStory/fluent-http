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

import java.nio.file.*;

import org.assertj.core.api.*;
import org.junit.*;

public class CompilersTest {
  @Test
  public void compile_less_file() {
    String css = Compilers.INSTANCE.compile(Paths.get("style.less"), "body { h1 { color: red; } }").getContent();

    assertThat(css).isEqualTo("body h1 {\n  color: red;\n}\n/*# sourceMappingURL=style.css.map */\n");
  }

  @Test
  public void do_not_compile_plain_file() {
    String css = Compilers.INSTANCE.compile(Paths.get("plain.txt"), "Hello").getContent();

    assertThat(css).isEqualTo("Hello");
  }

  @Test
  public void register_custom_compiler() {
    Compilers.INSTANCE.register(() -> new Compiler() {
      @Override
      public String compile(Path path, String source) {
        return source + source;
      }
    }, ".copycat");

    String source = Compilers.INSTANCE.compile(Paths.get("file.copycat"), "Hello").getContent();

    Assertions.assertThat(source).isEqualTo("HelloHello");
  }
}
