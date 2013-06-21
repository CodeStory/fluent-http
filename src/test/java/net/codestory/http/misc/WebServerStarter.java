package net.codestory.http.misc;

import java.util.*;

import net.codestory.http.*;

import org.junit.rules.*;

public class WebServerStarter extends ExternalResource {
  private static final int TRY_COUNT = 10;
  private static final int DEFAULT_PORT = 8183;

  private final WebServer webServer;
  private final Random random = new Random();

  private int port;
  private boolean started;

  public WebServerStarter() {
    webServer = new WebServer();
  }

  public int port() {
    ensureStarted();
    return port;
  }

  @Override
  protected void after() {
    stop();
  }

  private void ensureStarted() {
    if (started) {
      return;
    }

    for (int i = 0; i < TRY_COUNT; i++) {
      try {
        port = randomPort();
        webServer.start(port);
        started = true;
        return;
      } catch (Exception e) {
        System.err.println("Unable to bind server: " + e);
      }
    }

    throw new IllegalStateException("Unable to start server");
  }

  private void stop() {
    if (started) {
      webServer.stop();
      started = false;
    }
  }

  private int randomPort() {
    return DEFAULT_PORT + random.nextInt(1000);
  }
}
