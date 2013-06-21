package net.codestory.http.misc;

import java.util.*;

import net.codestory.http.*;

import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

public class WebServerRule extends WebServer implements TestRule {
  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        startOnRandomPort();
        try {
          base.evaluate();
        } finally {
          stop(); // TODO: in separated thread to make it faster ???????
        }
      }
    };
  }

  private void startOnRandomPort() {
    Random random = new Random();

    for (int i = 0; i < 20; i++) {
      try {
        int port = 8183 + random.nextInt(1000);
        start(port);
        return;
      } catch (Exception e) {
        System.err.println("Unable to bind server: " + e);
      }
    }

    throw new IllegalStateException("Unable to start server");
  }
}
