/**
 * Copyright (C) 2013-2014 all@code-story.net
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
package net.codestory.http.extensions;

import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.templating.Site;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.io.IOException;

public class CustomPayloadWriterTest extends AbstractProdWebServerTest {
  @Test
  public void add_resolver() {
    configure(routes -> routes
      .get("/", new CustomPayload())
      .setExtensions(new Extensions() {
        @Override
        public PayloadWriter createPayloadWriter(Request request, Response response, Env env, Site site, CompilerFacade compilers) {
          return new CustomPayloadWriter(request, response, env, site, compilers);
        }
      }));

    get("/").should().haveType("text/html;charset=UTF-8").contain("Hello World");
  }

  static class CustomPayloadWriter extends PayloadWriter {
    public CustomPayloadWriter(Request request, Response response, Env env, Site site, CompilerFacade compilers) {
      super(request, response, env, site, compilers);
    }

    @Override
    protected String getContentType(Object payload, String uri) {
      if (payload instanceof CustomPayload) {
        return "text/html;charset=UTF-8";
      }
      return super.getContentType(payload, uri);
    }

    @Override
    protected byte[] getData(Object payload, String uri) throws IOException {
      if (payload instanceof CustomPayload) {
        return forString(((CustomPayload) payload).content());
      }
      return super.getData(payload, uri);
    }
  }

  static class CustomPayload {
    public String content() {
      return "Hello World";
    }
  }
}
