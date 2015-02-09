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
package net.codestory.http;

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.util.concurrent.*;

public class AsyncResponseTest extends AbstractProdWebServerTest {

  ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Test
  public void waits_for_completion() {
    final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "test", executorService);
    configure(routes -> routes.get("/", context -> future));

    get("/").should().respond(200).contain("test");
  }

  @Test
  public void uses_expected_converters() {
    final CompletableFuture<Pojo> future = CompletableFuture.supplyAsync(() -> new Pojo("coucou"), executorService);
    configure(routes -> routes.get("/", context -> future));

    get("/").should().respond(200).haveType("application/json").contain("{\"name\":\"coucou\"}");
  }

  @Test
  public void deals_with_error() throws InterruptedException {
    final CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
      throw new RuntimeException("plop");
    }, executorService);

    configure(routes -> routes.get("/", context -> future));

    get("/").should().respond(500);
  }

  public static class Pojo {
    public Pojo(String name) {
      this.name = name;
    }

    public String name;
  }
}
