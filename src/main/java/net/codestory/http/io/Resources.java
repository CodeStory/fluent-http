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

import static java.nio.charset.StandardCharsets.*;
import static net.codestory.http.io.ClassPaths.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.compilers.*;
import net.codestory.http.misc.*;

public class Resources implements Serializable {
  private static final String[] TEMPLATE_EXTENSIONS = {"", ".html", ".md", ".markdown", ".txt"};

  private final Env env;

  public Resources(Env env) {
    this.env = env;
  }

  public SourceFile sourceFile(Path path) throws IOException {
    return new SourceFile(path, read(path, UTF_8));
  }

  public boolean isPublic(Path path) {
    for (Path part : path) {
      if ("..".equals(part.toString()) || part.toString().startsWith("_")) {
        return false;
      }
    }
    return exists(path);
  }

  public Path findExistingPath(String uri) {
    if (!uri.endsWith("/")) {
      // Try with extension
      for (String extension : TEMPLATE_EXTENSIONS) {
        Path templatePath = Paths.get(uri + extension);
        if (exists(templatePath)) {
          return templatePath;
        }
      }
    }

    // Try index
    for (String extension : TEMPLATE_EXTENSIONS) {
      Path templatePath = "/".equals(uri) ? Paths.get(uri + "index" + extension) : Paths.get(uri, "index" + extension);
      if (exists(templatePath)) {
        return templatePath;
      }
    }

    return null;
  }

  public long lastModified(Path path) {
    String pathWithPrefix = withPrefix(path);
    if (existsInFileSystem(pathWithPrefix)) {
      return file(pathWithPrefix).lastModified();
    }

    File file = fileForClasspath(getResource(pathWithPrefix));
    if (file != null) {
      return file.lastModified();
    }

    return -1;
  }

  public boolean exists(Path path) {
    String pathWithPrefix = withPrefix(path);
    return existsInFileSystem(pathWithPrefix) || existsInClassPath(pathWithPrefix);
  }

  public String read(Path path, Charset charset) throws IOException {
    String pathWithPrefix = withPrefix(path);
    return existsInFileSystem(pathWithPrefix) ? readFile(file(pathWithPrefix), charset) : readClasspath(pathWithPrefix, charset);
  }

  public byte[] readBytes(Path path) throws IOException {
    String pathWithPrefix = withPrefix(path);
    return existsInFileSystem(pathWithPrefix) ? readFileBytes(file(pathWithPrefix)) : readClasspathBytes(pathWithPrefix);
  }

  // static

  public static String relativePath(Path parent, Path path) {
    return toUnixString(parent.relativize(path));
  }

  public static String toUnixString(Path path) {
    return path.toString().replace('\\', '/');
  }

  // private

  private String withPrefix(Path path) {
    return toUnixString(Paths.get(env.appFolder(), path.toString()));
  }

  private boolean existsInClassPath(String path) {
    URL url = getResource(path);
    if (url == null) {
      return false;
    }

    File file = fileForClasspath(url);
    return (file == null) || file.isFile();
  }

  private String readClasspath(String path, Charset charset) throws IOException {
    URL url = getResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Classpath resource not found classpath:" + path);
    }

    File file = fileForClasspath(url);
    if (file != null) {
      return readFile(file, charset);
    }

    try (InputStream from = url.openStream()) {
      return InputStreams.readString(from, charset);
    }
  }

  private byte[] readClasspathBytes(String path) throws IOException {
    URL url = getResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Invalid file classpath: " + path);
    }

    File file = fileForClasspath(url);
    if (file != null) {
      if (!file.isFile()) {
        throw new IllegalArgumentException("Invalid file classpath: " + path);
      }
      return readFileBytes(file);
    }

    try (InputStream from = url.openStream()) {
      return InputStreams.readBytes(from);
    }
  }

  // Visible for testing
  File fileForClasspath(URL url) {
    String filename = url.getFile();
    if ((filename == null) || filename.contains(".jar!")) {
      return null;
    }

    try {
      String path = URLDecoder.decode(filename, "US-ASCII");

      // Search for file in sources instead of target to speed up live reload
      String sourcePath = Paths.get("src/main/resources/", env.appFolder(), Strings.substringAfter(path, '/' + env.appFolder() + '/')).toString();
      File file = new File(sourcePath);
      if (file.exists()) {
        return file;
      }

      return new File(path);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Invalid filename classpath: " + url, e);
    }
  }

  private File file(String path) {
    return new File(env.workingDir(), path);
  }

  private boolean existsInFileSystem(String path) {
    return file(path).isFile();
  }

  private String readFile(File file, Charset charset) throws IOException {
    if (!file.isFile()) {
      throw new IllegalArgumentException("Invalid file path: " + file);
    }

    try (InputStream from = new FileInputStream(file)) {
      return InputStreams.readString(from, charset);
    }
  }

  private byte[] readFileBytes(File file) throws IOException {
    if (!file.isFile()) {
      throw new IllegalArgumentException("Invalid file path: " + file);
    }

    try (InputStream from = new FileInputStream(file)) {
      return InputStreams.readBytes(from);
    }
  }
}
