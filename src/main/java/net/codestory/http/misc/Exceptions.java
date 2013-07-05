package net.codestory.http.misc;

import java.io.*;

public class Exceptions {
  private Exceptions() {
    // Static utility class
  }

  public static String toString(Exception error) {
    StringWriter string = new StringWriter();

    try (PrintWriter message = new PrintWriter(string)) {
      error.printStackTrace(message);
    }

    return string.toString();
  }
}
