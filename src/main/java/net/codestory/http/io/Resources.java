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

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;

public class Resources {
  private Resources() {
    // Static utility class
  }

  public static boolean exists(Path path) {
    String pathWithPrefix = withPrefix(path);
    return existsInClassPath(pathWithPrefix) || existsInFileSystem(pathWithPrefix);
  }

  public static String read(Path path, Charset charset) throws IOException {
    String pathWithPrefix = withPrefix(path);
    return existsInFileSystem(pathWithPrefix) ? readFile(pathWithPrefix, charset) : readClasspath(pathWithPrefix, charset);
  }

  public static byte[] readBytes(Path path) throws IOException {
    String pathWithPrefix = withPrefix(path);
    return existsInFileSystem(pathWithPrefix) ? readFileBytes(pathWithPrefix) : readClasspathBytes(pathWithPrefix);
  }

  private static String withPrefix(Path path) {
    return path.toString().startsWith("/") ? "web" + path : "web/" + path;
  }

  private static boolean existsInClassPath(String path) {
    URL url = ClassLoader.getSystemResource(path);
    if (url == null) {
      return false;
    }

    File file = fileForClasspath(url);
    return (file == null) || file.isFile();
  }

  private static boolean existsInFileSystem(String path) {
    return new File(path).isFile();
  }

  private static String readClasspath(String path, Charset charset) throws IOException {
    URL url = ClassLoader.getSystemResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Classpath resource not found classpath:" + path);
    }

    File file = fileForClasspath(url);
    if (file != null) {
      if (!file.isFile()) {
        throw new IllegalArgumentException("Invalid file classpath: " + path);
      }
      return readFile(file.getAbsolutePath(), charset);
    }

    try (InputStream from = url.openStream()) {
      return InputStreams.readString(from, charset);
    }
  }

  private static byte[] readClasspathBytes(String path) throws IOException {
    URL url = ClassLoader.getSystemResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Invalid file classpath: " + path);
    }

    File file = fileForClasspath(url);
    if (file != null) {
      if (!file.isFile()) {
        throw new IllegalArgumentException("Invalid file classpath: " + path);
      }
      return readFileBytes(file.getAbsolutePath());
    }

    try (InputStream from = url.openStream()) {
      return InputStreams.readBytes(from);
    }
  }

  private static String readFile(String path, Charset charset) throws IOException {
    return new String(readFileBytes(path), charset);
  }

  private static byte[] readFileBytes(String path) throws IOException {
    if (!new File(path).isFile()) {
      throw new IllegalArgumentException("Invalid file path: " + path);
    }
    return Files.readAllBytes(Paths.get(path));
  }

  private static File fileForClasspath(URL url) {
    String filename = url.getFile();

    if ((filename == null) || filename.contains(".jar!")) {
      return null;
    }

    try {
      return new File(URLDecoder.decode(filename, "US-ASCII").replace("/target/classes/", "/src/main/resources/"));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Invalid filename classpath: " + url, e);
    }
  }
}
