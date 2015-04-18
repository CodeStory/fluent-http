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
package net.codestory.http.io;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.walkFileTree;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static net.codestory.http.io.FileVisitor.onFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ClassPaths {
  private ClassPaths() {
    // Utility class
  }

  public static List<Path> classpathFolders() {
    URL[] urls = getUrls(Thread.currentThread().getContextClassLoader());
    return of(urls).map(url -> Paths.get(toUri(url))).collect(toList());
  }

  public static URL[] getUrls(ClassLoader parent) {
    if (!(parent instanceof URLClassLoader)) {
      return new URL[0];
    }
    return of(((URLClassLoader) parent).getURLs()).filter(url -> !url.toString().endsWith(".jar")).toArray(URL[]::new);
  }

  public static URL getResource(String path) {
    return Thread.currentThread().getContextClassLoader().getResource(path);
  }

  public static List<String> fromURL(URL url) {
    String protocol = url.getProtocol();
    String name = url.toExternalForm();

    if ("file".equals(protocol)) {
      try {
        File file = getFile(url);
        if (file != null) {
          return name.contains(".jar") ? forJarFile(new JarFile(file)) : forSystemDir(file);
        }
      } catch (Exception e) {
        // Ignore
      }
    }

    if ("jar".equals(protocol)) {
      try {
        List<String> relativePaths = forJarUrl(url);
        if (relativePaths != null) {
          return relativePaths;
        }
      } catch (Exception e) {
        // Ignore
      }
    }

    return emptyList();
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
      String path = URLDecoder.decode(url.getPath(), UTF_8.displayName());
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
    } catch (Exception e) {
      // Ignore
    }

    File file = getFile(url);
    if (file == null) {
      return null;
    }
    return forJarFile(new JarFile(file));
  }

  private static List<String> forJarFile(JarFile jarFile) {
    return jarFile.stream().filter(entry -> !entry.isDirectory()).map(entry -> entry.getName()).collect(toList());
  }

  private static List<String> forSystemDir(File file) throws IOException {
    if (file == null || !file.exists()) {
      return emptyList();
    }

    Path parent = file.toPath();

    List<String> files = new ArrayList<>();
    walkFileTree(parent, onFile(path -> files.add(Resources.relativePath(parent, path))));
    return files;
  }

  private static URI toUri(URL url) {
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Unable to convert URL to URI: " + url, e);
    }
  }
}
