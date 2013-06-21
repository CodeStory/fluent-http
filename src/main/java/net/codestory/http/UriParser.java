package net.codestory.http;

import java.util.*;

public class UriParser {
  private final String[] patternParts;

  public UriParser(String uriPattern) {
    this.patternParts = parts(uriPattern);
  }

  public String[] params(String uri) {
    String[] uriParts = parts(uri);

    List<String> result = new ArrayList<>(); // TODO : optimize

    for (int i = 0; i < patternParts.length; i++) {
      String patternPart = patternParts[i];

      if (patternPart.startsWith(":")) {
        String uriPart = uriParts[i];
        result.add(uriPart);
      }
    }

    return result.toArray(new String[result.size()]);
  }

  public boolean matches(String uri) {
    String[] uriParts = parts(uri);
    if (patternParts.length != uriParts.length) {
      return false;
    }

    for (int i = 0; i < patternParts.length; i++) {
      String patternPart = patternParts[i];
      String uriPart = uriParts[i];

      if (!patternPart.startsWith(":") && !patternPart.equals(uriPart)) {
        return false;
      }
    }

    return true;
  }

  static String[] parts(String uri) {
    return uri.split("[/]");
  }
}
