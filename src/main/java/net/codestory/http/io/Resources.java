package net.codestory.http.io;

import java.io.*;
import java.nio.charset.*;

public class Resources {
  private static final int BUF_SIZE = 0x1000; // 4K

  private Resources() {
    // Static utility class
  }

  public static String toString(String name, Charset charset) throws IOException {
    try (InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(name)) {
      if (null == stream) {
        return "";
      }

      StringBuilder string = new StringBuilder();

      try (Reader reader = new InputStreamReader(stream, charset)) {
        while (true) {
          char[] buffer = new char[BUF_SIZE];
          int r = reader.read(buffer);
          if (r == -1) {
            break;
          }
          string.append(buffer, 0, r);
        }
      }

      return string.toString();
    }
  }
}
