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
package net.codestory.http.logs;

import static net.codestory.http.io.Strings.*;

import java.io.*;
import java.util.*;

import org.slf4j.*;

public class Logs {
  private final static LogsImplementation logs = Boolean.getBoolean("PROD_MODE") ? new Slf4jLogs() : new ConsoleLogs();

  private Logs() {
    // static class
  }

  public static void unableToBindServer(Exception e) {
    logs.error("Unable to bind server", e);
  }

  public static void mode(boolean production) {
    logs.info(production ? "Production mode" : "Dev mode");
  }

  public static void started(int port) {
    logs.info("Server started on port {}", port);
  }

  public static void compilerError(Exception e) {
    logs.error(e.getMessage());
  }

  public static void unableToServerErrorPage(Exception e) {
    logs.error("Unable to serve an error page", e);
  }

  public static void cachingOnDisk(File path) {
    logs.info("Caching on disk @ {}", path.getAbsolutePath());
  }

  public static void uri(String uri) {
    logs.info(uri);
  }

  public static void reloadingConfiguration() {
    logs.info("Reloading configuration...");
  }

  public static void printKnownWebjars(Collection<String> uris) {
    logs.info("Found these webjars files:");
    for (String uri : uris) {
      logs.info(" + " + substringAfter(uri, "META-INF/resources"));
    }
  }

  private static interface LogsImplementation {
    void info(String message);

    void info(String message, Object argument);

    void error(String message);

    void error(String message, Throwable e);
  }

  private static class ConsoleLogs implements LogsImplementation {
    @Override
    public void info(String message) {
      System.out.println(message);
    }

    @Override
    public void info(String message, Object argument) {
      System.out.println(message.replace("{}", argument.toString()));
    }

    @Override
    public void error(String message) {
      System.err.println(message);
    }

    @Override
    public void error(String message, Throwable e) {
      System.err.println(message);
      e.printStackTrace();
    }
  }

  private static class Slf4jLogs implements LogsImplementation {
    private final static Logger LOG = LoggerFactory.getLogger("Fluent");

    @Override
    public void info(String message) {
      LOG.info(message);
    }

    @Override
    public void info(String message, Object argument) {
      LOG.info(message, argument);
    }

    @Override
    public void error(String message) {
      LOG.error(message);
    }

    @Override
    public void error(String message, Throwable e) {
      LOG.error(message, e);
    }
  }
}
