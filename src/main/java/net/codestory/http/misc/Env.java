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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Env {
  private final File workingDir;
  private final boolean prodMode;
  private final boolean classPath;
  private final boolean filesystem;
  private final boolean gzip;

  public Env() {
    this.workingDir = new File(".");
    this.prodMode = getBoolean("PROD_MODE", false);
    this.classPath = !getBoolean("http.disable.classpath", false);
    this.filesystem = !getBoolean("http.disable.filesystem", false);
    this.gzip = !getBoolean("http.disable.gzip", false);
  }

  private Env(File workingDir, boolean prodMode, boolean classPath, boolean filesystem, boolean gzip) {
    this.workingDir = workingDir;
    this.prodMode = prodMode;
    this.classPath = classPath;
    this.filesystem = filesystem;
    this.gzip = gzip;
  }

  // helper factories

  public static Env prod() {
    return new Env(new File("."), true, true, true, true);
  }

  public static Env dev() {
    return new Env(new File("."), false, true, true, false);
  }

  public Env withWorkingDir(File newWorkingDir) {
    return new Env(newWorkingDir, prodMode, classPath, filesystem, gzip);
  }

  public Env withProdMode(boolean newProdMode) {
    return new Env(workingDir, newProdMode, classPath, filesystem, gzip);
  }

  public Env withClassPath(boolean scanCassPath) {
    return new Env(workingDir, prodMode, scanCassPath, filesystem, gzip);
  }

  public Env withFilesystem(boolean scanFilesystem) {
    return new Env(workingDir, prodMode, classPath, scanFilesystem, gzip);
  }

  public Env withGzip(boolean gzipResponse) {
    return new Env(workingDir, prodMode, classPath, filesystem, gzipResponse);
  }

  //

  public File workingDir() {
    return workingDir;
  }

  public String appFolder() {
    return "app";
  }

  public List<Path> foldersToWatch() {
    List<Path> folders = new ArrayList<>();
    if (classPath) {
      folders.addAll(classpathFolders());
    }
    if (filesystem) {
      folders.add(new File(workingDir, appFolder()).toPath());
    }
    return folders;
  }

  public boolean prodMode() {
    return prodMode;
  }

  public int overriddenPort(int port) {
    return getInt("PORT", port);
  }

  public boolean classPath() {
    return classPath;
  }

  public boolean filesystem() {
    return filesystem;
  }

  public boolean gzip() {
    return gzip;
  }

  private static String get(String propertyName) {
    String env = System.getenv(propertyName);
    return (env != null) ? env : System.getProperty(propertyName);
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
