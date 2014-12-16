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

import static java.nio.charset.StandardCharsets.*;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.IntStream.*;

import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.codestory.http.testhelpers.*;

import org.junit.*;

public class StreamTest extends AbstractProdWebServerTest {
  @Test
  public void server_sent_events() {
    configure(routes -> routes.get("/events", () -> range(1, 1000).mapToObj(i -> "MESSAGE" + i)));

    get("/events").should().contain("data: MESSAGE1\n\n" + "data: MESSAGE2\n\n" + "data: MESSAGE3\n\n");
  }

  @Test
  public void server_sent_events_multiline() {
    configure(routes -> routes.get("/events", () -> range(1, 1000).mapToObj(i -> "MESSAGE\n" + i)));

    get("/events").should().contain("data: MESSAGE\ndata: 1\n\n" + "data: MESSAGE\ndata: 2\n\n" + "data: MESSAGE\ndata: 3\n\n");
  }

  @Test
  public void stream() {
    byte[] buffer = "Hello World".getBytes(UTF_8);

    configure(routes -> routes.get("/stream", () -> new ByteArrayInputStream(buffer)));

    get("/stream").should().contain("Hello World");
  }

  @Test(timeout = 5000)
  public void support_multiple_clients_in_parallel() {
    byte[] buffer = "Hello World".getBytes(UTF_8);

    configure(routes -> routes
        .get("/blocking", () -> Stream.generate(() -> {
          try {
            HOURS.sleep(1);
          } catch (InterruptedException e) {
            // Ignore
          }
          return "OK";
        }).limit(1))
        .get("/stream", () -> new ByteArrayInputStream(buffer))
    );

    new Thread(() -> get("/blocking").should().contain("OK")).start();

    range(1, 1000).forEach(i -> get("/stream").should().contain("Hello World"));
  }
}
