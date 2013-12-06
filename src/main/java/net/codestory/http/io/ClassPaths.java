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
package net.codestory.http.io;

import static java.nio.file.Files.*;
import static net.codestory.http.io.FileVisitors.*;
import static net.codestory.http.io.Resources.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

class ClassPaths {
  static List<String> fromURL(URL url) {
    String protocol = url.getProtocol();
    String name = url.toExternalForm();

    if ("file".equals(protocol)) {
      try {
        File file = getFile(url);
        return name.contains(".jar") ? forJarFile(new JarFile(file)) : forSystemDir(file);
      } catch (Throwable e) {
        // Ignore
      }
    }

    try {
      if ("jar".equals(protocol)) {
        List<String> relativePaths = forJarUrl(url);
        if (relativePaths != null) {
          return relativePaths;
        }
      }
    } catch (Throwable e) {
      // Ignore
    }

    return Collections.emptyList();
  }

  private static File getFile(URL url) {
    try {
      String path = url.toURI().getSchemeSpecificPart();
      if (new File(path).exists()) {
        return new File(path);
      }
    } catch (URISyntaxException e) {
      // Ignore
    }

    try {
      String path = URLDecoder.decode(url.getPath(), "UTF-8");
      if (path.contains(".jar!")) {
        path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
      }
      if (new File(path).exists()) {
        return new File(path);
      }
    } catch (UnsupportedEncodingException e) {
      // Ignore
    }

    try {
      String path = url.toExternalForm();
      if (path.startsWith("jar:")) {
        path = path.substring("jar:".length());
      } else if (path.startsWith("file:")) {
        path = path.substring("file:".length());
      }
      if (path.contains(".jar!")) {
        path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
      }
      if (new File(path).exists()) {
        return new File(path);
      }

      path = path.replace("%20", " ");
      if (new File(path).exists()) {
        return new File(path);
      }
    } catch (Exception e) {
      // Ignore
    }

    return null;
  }

  private static List<String> forJarUrl(URL url) throws IOException {
    try {
      URLConnection urlConnection = url.openConnection();
      if (urlConnection instanceof JarURLConnection) {
        return forJarFile(((JarURLConnection) urlConnection).getJarFile());
      }
    } catch (Throwable e) {
      // Ignore
    }

    File file = getFile(url);
    if (file == null) {
      return null;
    }
    return forJarFile(new JarFile(file));
  }

  private static List<String> forJarFile(JarFile jarFile) {
    List<String> files = new ArrayList<>();

    Enumeration<? extends ZipEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (!entry.isDirectory()) {
        files.add(entry.getName());
      }
    }

    return files;
  }

  private static List<String> forSystemDir(File file) throws IOException {
    if (file == null || !file.exists()) {
      return Collections.emptyList();
    }

    Path parent = file.toPath();

    List<String> files = new ArrayList<>();
    walkFileTree(parent, onFile(path -> files.add(relativePath(parent, path))));
    return files;
  }
}
