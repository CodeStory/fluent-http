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

  public static String extension(Path path) {
    return Strings.substringAfterLast(path.toString(), ".");
  }

  public static String type(Path path) {
    return path.toString().startsWith("classpath:") ? "classpath:" : "";
  }

  public static boolean exists(Path path) {
    if ("classpath:".equals(type(path))) {
      URL url = ClassLoader.getSystemResource(path.toString().substring(10));
      if (url == null) {
        return false;
      }

      File file = fileForClasspath(url);
      return (file == null) || file.isFile();

    }
    return path.toFile().isFile();
  }

  public static String read(Path path, Charset charset) throws IOException {
    if ("classpath:".equals(type(path))) {
      return readClasspath(path.toString().substring(10), charset);
    }
    return readFile(path, charset);
  }

  public static byte[] readBytes(Path path) throws IOException {
    if ("classpath:".equals(type(path))) {
      return readClasspathBytes(path.toString().substring(10));
    }
    return readFileBytes(path);
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
      return readFile(file.toPath(), charset);
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
      return readFileBytes(file.toPath());
    }

    try (InputStream from = url.openStream()) {
      return InputStreams.readBytes(from);
    }
  }

  private static String readFile(Path path, Charset charset) throws IOException {
    return new String(readFileBytes(path), charset);
  }

  private static byte[] readFileBytes(Path path) throws IOException {
    if (!path.toFile().isFile()) {
      throw new IllegalArgumentException("Invalid file path: " + path);
    }
    return Files.readAllBytes(path);
  }

  private static File fileForClasspath(URL url) {
    if (url.getFile() == null) {
      return null;
    }
    return new File(url.getFile().replace("/target/classes/", "/src/main/resources/"));
  }
}
