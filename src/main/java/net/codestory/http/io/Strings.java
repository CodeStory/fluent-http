package net.codestory.http.io;

public class Strings {
  private Strings() {
    // Static utility class
  }

  public static int countMatches(String in, String what) {
    int count = 0;

    int index = in.indexOf(what);
    while (index != -1) {
      count++;
      index = in.indexOf(what, index + what.length());
    }

    return count;
  }

  public static String substringAfter(String in, String what) {
    int index = in.indexOf(what);
    if (index == -1) {
      return "";
    }
    return in.substring(index + what.length());
  }

  public static String substringBefore(String in, String what) {
    int index = in.indexOf(what);
    if (index == -1) {
      return in;
    }
    return in.substring(0, index);
  }

  public static String substringBetween(String in, String start, String end) {
    int indexStart = in.indexOf(start);
    if (indexStart == -1) {
      return "";
    }

    int indexEnd = in.indexOf(end, indexStart + start.length());
    if (indexEnd == -1) {
      return "";
    }

    return in.substring(indexStart + start.length(), indexEnd);
  }
}
