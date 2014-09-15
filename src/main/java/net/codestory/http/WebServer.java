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

import static java.util.Arrays.*;
import static net.codestory.http.Configuration.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.filters.log.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.reload.*;
import net.codestory.http.ssl.*;

public class WebServer extends AbstractWebServer {
  private final HttpServerWrapper server;
  private int port;

  public WebServer() {
    this(NO_ROUTE);
  }

  public WebServer(Class<? extends Configuration> configuration) {
    this(new ConfigurationReloadingProxy(configuration));
  }

  public WebServer(Configuration configuration) {
    try {
      server = new SimpleServerWrapper(this::handle);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create http server", e);
    }
    configure(configuration);
  }

  public static void main(String[] args) throws Exception {
    new WebServer(routes -> routes
      .filter(new LogRequestFilter()))
      .start(8080);
  }

  public WebServer startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(10000);
        start(port);
        return this;
      } catch (Exception e) {
        LOG.error("Unable to bind server", e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  public WebServer start() {
    return start(8080);
  }

  public WebServer start(int port) {
    return startWithContext(port, null, false);
  }

  public WebServer startSSL(int port, Path pathCertificate, Path pathPrivateKey) {
    return startSSL(port, asList(pathCertificate), pathPrivateKey, null);
  }

  public WebServer startSSL(int port, List<Path> pathChain, Path pathPrivateKey) {
    return startSSL(port, pathChain, pathPrivateKey, null);
  }

  public WebServer startSSL(int port, List<Path> pathChain, Path pathPrivateKey, List<Path> pathTrustAnchors) {
    SSLContext context;
    try {
      context = new SSLContextFactory().create(pathChain, pathPrivateKey, pathTrustAnchors);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to read certificate or key", e);
    }
    boolean authReq = pathTrustAnchors != null;
    return startWithContext(port, context, authReq);
  }

  protected WebServer startWithContext(int port, SSLContext context, boolean authReq) {
    this.port = Env.get().overriddenPort(port);

    try {
      LOG.info(Env.get().prodMode() ? "Production mode" : "Dev mode");

      server.start(this.port, context, authReq);

      LOG.info("Server started on port {}", this.port);
    } catch (RuntimeException e) {
      throw e;
    } catch (BindException e) {
      throw new IllegalStateException("Port already in use " + this.port);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to bind the web server on port " + this.port, e);
    }

    return this;
  }

  public int port() {
    return port;
  }

  public void reset() {
    configure(NO_ROUTE);
  }

  public void stop() {
    try {
      server.stop();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to stop the web server", e);
    }
  }
}
