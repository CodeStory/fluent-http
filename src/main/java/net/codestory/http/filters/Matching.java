package net.codestory.http.filters;

public enum Matching {
  MATCH, WRONG_METHOD, WRONG_URL;

  public boolean isBetter(Matching other) {
    return ordinal() < other.ordinal();
  }
}
