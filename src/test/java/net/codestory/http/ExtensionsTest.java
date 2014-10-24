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

import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.extensions.Extensions;
import net.codestory.http.misc.Env;
import net.codestory.http.templating.BasicResolver;
import net.codestory.http.templating.Model;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

public class ExtensionsTest extends AbstractProdWebServerTest {
  @Test
  public void add_handlebars_resolver() {
    server.configure(routes -> routes.setExtensions(new Extensions() {
      @Override
      public void configureCompilers(CompilerFacade compilers, Env env) {
        compilers.addHandlebarResolver(new HelloWorldResolver());
      }
    }));

    get("/extensions/custom_resolver").produces("Hello World");
  }

  @Test
  public void configure_handlebars() {
    server.configure(routes -> routes
      .get("/extensions/custom_delimiters", () -> Model.of("name", "Bob"))
      .setExtensions(new Extensions() {
        @Override
        public void configureCompilers(CompilerFacade compilers, Env env) {
          compilers.configureHandlebars(hb -> hb.startDelimiter("((").endDelimiter("))"));
        }
      }));

    get("/extensions/custom_delimiters").produces("Hello Bob");
  }

  static class HelloWorldResolver implements BasicResolver {
    @Override
    public String tag() {
      return "greeting";
    }

    @Override
    public Object resolve(Object context) {
      return "Hello World";
    }
  }
}
