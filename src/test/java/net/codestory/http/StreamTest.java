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

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.codestory.http.testhelpers.*;

import org.junit.*;

public class StreamTest extends AbstractProdWebServerTest {
  @Test
  public void server_sent_events() {
    AtomicLong index = new AtomicLong(0);
    Supplier<String> messageSupplier = () -> "MESSAGE" + index.incrementAndGet();

    server.configure(routes -> routes.get("/events", () -> Stream.generate(messageSupplier).limit(1000)));

    get("/events").produces("data: MESSAGE1\n\n" + "data: MESSAGE2\n\n" + "data: MESSAGE3\n\n");
  }

  @Test
  public void stream() {
    byte[] buffer = "Hello World".getBytes(UTF_8);

    server.configure(routes -> routes.get("/stream", () -> new ByteArrayInputStream(buffer)));

    get("/stream").produces("Hello World");
  }
}
