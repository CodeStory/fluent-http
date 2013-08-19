package net.codestory.http.filters;

public enum Match {
  OK, WRONG_METHOD, WRONG_URL;

  public boolean isBetter(Match other) {
    return ordinal() < other.ordinal();
  }
}
