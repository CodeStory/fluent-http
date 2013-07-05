package net.codestory.http.io;

import java.io.*;

public class Bytes {
  private static final int BUF_SIZE = 0x1000; // 4K

  private Bytes() {
    // Static utility class
  }

  public static byte[] readBytes(InputStream from) throws IOException {
    try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[BUF_SIZE];
      while (true) {
        int r = from.read(buffer);
        if (r == -1) {
          break;
        }
        bytes.write(buffer, 0, r);
      }

      return bytes.toByteArray();
    }
  }
}
