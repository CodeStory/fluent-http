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
package net.codestory.http.payload;

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AsyncResponseTest extends AbstractProdWebServerTest {
  ExecutorService executorService = Executors.newSingleThreadExecutor();

  private <T> CompletableFuture<T> future(Supplier<T> supplier) {
    return supplyAsync(supplier, executorService);
  }

  @Test
  public void waits_for_completion() {
    configure(routes -> routes.get("/", () -> future(() -> "test")));

    get("/").should().respond(200).contain("test");
  }

  @Test
  public void uses_expected_converters() {
    configure(routes -> routes.get("/", () -> future(() -> new Pojo("coucou"))));

    get("/").should().respond(200).haveType("application/json").contain("{\"name\":\"coucou\"}");
  }

  @Test
  public void deals_with_error() {
    configure(routes -> routes.get("/", () -> future(() -> {
      throw new RuntimeException("plop");
    })));

    get("/").should().respond(500);
  }

  static class Pojo {
    Pojo(String name) {
      this.name = name;
    }

    String name;
  }
}
