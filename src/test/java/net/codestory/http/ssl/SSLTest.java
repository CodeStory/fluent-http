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
package net.codestory.http.ssl;

import java.nio.file.*;
import java.util.*;

import net.codestory.http.*;

import org.junit.*;

import com.google.common.io.*;

public class SSLTest {
  @Test
  public void start_server() {
    Path pathCertificate = Paths.get(Resources.getResource("certificates/server.crt").getPath());
    Path pathPrivateKey = Paths.get(Resources.getResource("certificates/server.der").getPath());

    WebServer webServer = new WebServer(routes -> routes.get("/", () -> "Hello"));
    webServer.startSSL(8183 + new Random().nextInt(1000), pathCertificate, pathPrivateKey);
  }
}