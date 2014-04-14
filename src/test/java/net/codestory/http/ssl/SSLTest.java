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

import java.net.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.io.*;

import org.junit.*;

public class SSLTest {
  @Test
  public void start_server() throws URISyntaxException {
    Path pathCertificate = resource("certificates/server.crt");
    Path pathPrivateKey = resource("certificates/server.der");

    WebServer webServer = new WebServer();
    webServer.startSSL(8183 + new Random().nextInt(1000), pathCertificate, pathPrivateKey);
  }

  private static Path resource(String name) throws URISyntaxException {
    return Paths.get(Resources.getResource(name).toURI());
  }
}