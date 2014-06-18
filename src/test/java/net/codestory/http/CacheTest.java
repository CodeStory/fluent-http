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

import java.io.*;

import net.codestory.http.testhelpers.*;

import org.junit.*;
import org.junit.rules.*;

public class CacheTest extends AbstractProdWebServerTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void etag() {
    server.configure(routes -> routes.get("/", "Hello"));

    get("/").produces(200, "text/html", "Hello").producesHeader("Etag", "8b1a9953c4611296a827abf8c47804d7");
    getWithHeader("/", "If-None-Match", "8b1a9953c4611296a827abf8c47804d7").produces(304);
    getWithHeader("/", "If-None-Match", "\"8b1a9953c4611296a827abf8c47804d7\"").produces(304);
  }

  private File createFile(String hello) {
    try {
      File file = temp.newFile();
      try (Writer writer = new FileWriter(file)) {
        writer.write(hello);
      }
      return file;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create temp file", e);
    }
  }
}
