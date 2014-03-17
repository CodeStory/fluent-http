package net.codestory.http.filters.basic;

public interface Users {
  boolean isValid(String login, String password);
}
