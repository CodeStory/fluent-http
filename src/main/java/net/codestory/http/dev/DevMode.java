package net.codestory.http.dev;

import net.codestory.http.*;

public class DevMode {
  private Configuration lastConfiguration;

  public static boolean isDevMode() {
    return !Boolean.parseBoolean(System.getProperty("PROD_MODE", "false"));
  }

  public void setLastConfiguration(Configuration configuration) {
    lastConfiguration = configuration;
  }

  public Configuration getLastConfiguration() {
    return lastConfiguration;
  }
}
