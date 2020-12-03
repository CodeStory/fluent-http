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
package net.codestory.http.testhelpers;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static java.lang.String.format;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProdWebServerRuleTest {
  @ClassRule public static ProdWebServerRule server1 = new ProdWebServerRule();
  @ClassRule public static ProdWebServerRule server2 = new ProdWebServerRule();
  OkHttpClient client = new OkHttpClient();

  @Test
  public void test_2_servers_rules_should_be_independent() throws IOException {
    server1.configure(routes -> routes.get("/index", "hello world 1"));
    server2.configure(routes -> routes.get("/index", "hello world 2"));
    try (Response response = client.newCall(new Request.Builder().url(format("http://localhost:%s/index", server1.port())).build()).execute()) {
        assertThat(response.body().string()).isEqualTo("hello world 1");
    }
    try (Response response = client.newCall(new Request.Builder().url(format("http://localhost:%s/index", server2.port())).build()).execute()) {
        assertThat(response.body().string()).isEqualTo("hello world 2");
    }
  }
}
