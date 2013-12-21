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
package net.codestory.http.misc;

public class Env {
  private Env() {
    // Static class
  }

  public static boolean devMode() {
    return getBoolean("PROD_MODE", false);
  }

  public static boolean disableClassPath() {
    return getBoolean("http.disable.classpath", false);
  }

  public static boolean disableFilesystem() {
    return getBoolean("http.disable.filesystem", false);
  }

  public static int overriddenPort(int port) {
    return Env.getInt("app.port", port);
  }

  private static String get(String name, String defaultValue) {
    String env = System.getenv(name);
    if (env != null) {
      return env;
    }
    return System.getProperty(name, defaultValue);
  }

  private static boolean getBoolean(String name, boolean defaultValue) {
    return Boolean.parseBoolean(Env.get(name, Boolean.toString(defaultValue)));
  }

  private static int getInt(String name, int defaultValue) {
    return Integer.parseInt(Env.get(name, Integer.toString(defaultValue)));
  }
}
