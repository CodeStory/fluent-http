/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import net.codestory.http.reload.MasterFolderWatch;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.codestory.http.io.ClassPaths.classpathFolders;
import static net.codestory.http.misc.MemoizingSupplier.memoize;

public class Env implements Serializable {
  private final File workingDir;
  private final boolean prodMode;
  private final boolean classPath;
  private final boolean filesystem;
  private final boolean gzip;
  private final boolean liveReloadServer;
  private final boolean injectLiveReloadScript;
  private final boolean diskCache;
  private final Supplier<MasterFolderWatch> folderWatch;
  private final String appFolder;

  public Env() {
    this(
      new File("."),
      getBoolean("PROD_MODE", false),
      !getBoolean("http.disable.classpath", false),
      !getBoolean("http.disable.filesystem", false),
      !getBoolean("http.disable.gzip", false),
      getBoolean("http.livereload.server", true),
      getBoolean("http.livereload.script", true),
      getBoolean("http.cache.disk", true),
      get("APP_FOLDER", "app")
    );
  }

  private Env(File workingDir, boolean prodMode, boolean classPath, boolean filesystem, boolean gzip, boolean liveReloadServer, boolean injectLiveReloadScript, boolean diskCache, String appFolder) {
    this.workingDir = workingDir;
    this.prodMode = prodMode;
    this.classPath = classPath;
    this.filesystem = filesystem;
    this.gzip = gzip;
    this.liveReloadServer = liveReloadServer;
    this.injectLiveReloadScript = injectLiveReloadScript;
    this.diskCache = diskCache;
    this.folderWatch = memoize(() -> new MasterFolderWatch(this));
    this.appFolder = appFolder;
  }

  // helper factories

  public static Env prod() {
    return new Env(new File("."), true, true, true, true, false, false, true, "app");
  }

  public static Env dev() {
    return new Env(new File("."), false, true, true, false, true, true, true, "app");
  }

  public static Env dev(File workingDir) { return new Env(workingDir, false, false, true, false, true, true, true, "app");}

  public Env withWorkingDir(File newWorkingDir) {
    return new Env(newWorkingDir, prodMode, classPath, filesystem, gzip, liveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withProdMode(boolean newProdMode) {
    return new Env(workingDir, newProdMode, classPath, filesystem, gzip, liveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withClassPath(boolean shouldScanCassPath) {
    return new Env(workingDir, prodMode, shouldScanCassPath, filesystem, gzip, liveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withFilesystem(boolean shouldScanFilesystem) {
    return new Env(workingDir, prodMode, classPath, shouldScanFilesystem, gzip, liveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withGzip(boolean shouldGzipResponse) {
    return new Env(workingDir, prodMode, classPath, filesystem, shouldGzipResponse, liveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withLiveReloadServer(boolean shouldStartLiveReloadServer) {
    return new Env(workingDir, prodMode, classPath, filesystem, gzip, shouldStartLiveReloadServer, injectLiveReloadScript, diskCache, appFolder);
  }

  public Env withInjectLiveReloadScript(boolean shouldInjectLiveReloadScript) {
    return new Env(workingDir, prodMode, classPath, filesystem, gzip, liveReloadServer, shouldInjectLiveReloadScript, diskCache, appFolder);
  }

  public Env withDiskCache(boolean shouldUseDiskCache) {
    return new Env(workingDir, prodMode, classPath, filesystem, gzip, liveReloadServer, injectLiveReloadScript, shouldUseDiskCache, appFolder);
  }

  public Env withAppFolder(String shouldAppFolder) {
    return new Env(workingDir, prodMode, classPath, filesystem, gzip, liveReloadServer, injectLiveReloadScript, diskCache, shouldAppFolder);
  }

  //

  public MasterFolderWatch folderWatcher() {
    return folderWatch.get();
  }

  public File workingDir() {
    return workingDir;
  }

  public String appFolder() {
    return appFolder;
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

  public boolean liveReloadServer() {
    return liveReloadServer;
  }

  public boolean injectLiveReloadScript() {
    return injectLiveReloadScript;
  }

  public boolean diskCache() {
    return diskCache;
  }

  private static String get(String propertyName) {
    String env = System.getenv(propertyName);
    return (env != null) ? env : System.getProperty(propertyName);
  }

  private static String get(String propertyName, String defaultValue) {
    String env = System.getenv(propertyName);
    return (env != null) ? env : System.getProperty(propertyName, defaultValue);
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
