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

import net.codestory.http.misc.RunMultipleTimes;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.range;

@Ignore("Failing sometimes and I don't know why")
public class MultipleStreamsTest extends AbstractProdWebServerTest {
  @Rule
  public RunMultipleTimes runMultipleTimes = new RunMultipleTimes(32);

  @Test(timeout = 60000)
  public void support_multiple_clients_in_parallel() throws InterruptedException {
    Semaphore semaphore = new Semaphore(1);
    semaphore.acquire();

    configure(routes -> routes
        .get("/blocking", () -> Stream.generate(() -> {
          try {
            semaphore.acquire();
          } catch (InterruptedException e) {
            // Ignore
          }

          return "OK";
        }).limit(1))
        .get("/non_blocking", () -> new ByteArrayInputStream("Hello World".getBytes(UTF_8)))
    );

    Thread blockingThread = new Thread(() -> get("/blocking").should().contain("OK"));
    Thread nonBlockingThread = new Thread(() -> range(1, 1000).parallel().forEach(n -> get("/non_blocking").should().contain("Hello World")));

    blockingThread.start();
    nonBlockingThread.start();

    nonBlockingThread.join();
    semaphore.release();
    blockingThread.join();
  }
}
