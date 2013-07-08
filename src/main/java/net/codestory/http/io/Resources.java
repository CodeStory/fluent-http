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
  private static final int BUF_SIZE = 0x1000; // 4K

  private Resources() {
    // Static utility class
  }

  public static String type(Path path) {
    if (path.toString().startsWith("classpath:")) {
      return "classpath:";
    }
    return "";
  }

  public static boolean exists(Path path) {
    if ("classpath:".equals(type(path))) {
      return ClassLoader.getSystemResource(path.toString().substring(10)) != null;
    }
    return path.toFile().exists();
  }

  public static String read(Path path, Charset charset) throws IOException {
    if ("classpath:".equals(type(path))) {
      return readClasspath(path.toString().substring(10), charset);
    }
    return readFile(path, charset);
  }

  private static String readClasspath(String path, Charset charset) throws IOException {
    URL url = ClassLoader.getSystemResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Invalid file classpath: " + path);
    }

    if (url.getFile() != null) {
      File file = new File(url.getFile());
      if (file.exists()) {
        return readFile(file.toPath(), charset);
      }
    }

    return Resources.read(url, charset);
  }

  private static String readFile(Path path, Charset charset) throws IOException {
    if (!path.toFile().exists()) {
      throw new IllegalArgumentException("Invalid file path: " + path);
    }

    return new String(Files.readAllBytes(path), charset);
  }

  private static String read(URL url, Charset charset) throws IOException {
    try (InputStream stream = url.openStream()) {
      StringBuilder string = new StringBuilder();
      char[] buffer = new char[BUF_SIZE];

      try (Reader from = new InputStreamReader(stream, charset)) {
        int count;
        while (-1 != (count = from.read(buffer))) {
          string.append(buffer, 0, count);
        }
      }

      return string.toString();
    }
  }
}
