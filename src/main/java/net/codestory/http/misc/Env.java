/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import static net.codestory.http.io.ClassPaths.classpathFolders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Env {
  public static final String DEFAULT_APP_FOLDER = "app";

  private final String appFolder;
  private final boolean prodMode;
  private final boolean disableClassPath;
  private final boolean disableFilesystem;
  private final boolean disableGzip;

  public Env() {
    this.appFolder = getString("APP_FOLDER", DEFAULT_APP_FOLDER);
    this.prodMode = getBoolean("PROD_MODE", false);
    this.disableClassPath = getBoolean("http.disable.classpath", false);
    this.disableFilesystem = getBoolean("http.disable.filesystem", false);
    this.disableGzip = getBoolean("http.disable.gzip", false);
  }

  public Env(String appFolder, boolean prodMode, boolean disableClassPath, boolean disableFilesystem, boolean disableGzip) {
    this.appFolder = appFolder;
    this.prodMode = prodMode;
    this.disableClassPath = disableClassPath;
    this.disableFilesystem = disableFilesystem;
    this.disableGzip = disableGzip;
  }

  // helper factories

  public static Env prod() {
    return new Env(DEFAULT_APP_FOLDER, true, false, false, false);
  }

  public static Env dev() {
    return new Env(DEFAULT_APP_FOLDER, false, false, false, true);
  }

  //

  public Path appPath() {
    return Paths.get(appFolder());
  }

  public String appFolder() {
    return appFolder;
  }

  public List<Path> foldersToWatch() {
    List<Path> folders = new ArrayList<>();
    if (!disableClassPath()) {
      folders.addAll(classpathFolders());
    }
    if (!disableFilesystem()) {
      folders.add(appPath());
    }
    return folders;
  }

  public boolean prodMode() {
    return prodMode;
  }

  public int overriddenPort(int port) {
    return getInt("PORT", port);
  }

  public boolean disableClassPath() {
    return disableClassPath;
  }

  public boolean disableFilesystem() {
    return disableFilesystem;
  }

  public boolean disableGzip() {
    return disableGzip;
  }

  private static String get(String propertyName) {
    String env = System.getenv(propertyName);
    return (env != null) ? env : System.getProperty(propertyName);
  }

  private static String getString(String propertyName, String defaultValue) {
    String value = get(propertyName);
    return (value == null) ? defaultValue : value;
  }

  private static boolean getBoolean(String propertyName, boolean defaultValue) {
    String value = get(propertyName);
    return (value == null) ? defaultValue : Boolean.parseBoolean(value);
  }

  private static int getInt(String propertyName, int defaultValue) {
    String value = get(propertyName);
    return (value == null) ? defaultValue : Integer.parseInt(value);
  }
}
