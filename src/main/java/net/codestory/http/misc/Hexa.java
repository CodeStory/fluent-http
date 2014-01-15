package net.codestory.http.misc;

public class Hexa {
  public static String toHex(byte[] hash) {
    StringBuilder result = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xFF & b);
      if (hex.length() == 1) {
        result.append("0");
      }
      result.append(hex);
    }
    return result.toString();
  }
}
