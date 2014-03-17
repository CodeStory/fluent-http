package net.codestory.http.filters.basic;

import java.util.*;

public interface Users {
  boolean isValid(String login, String password);

  static Users forMap(Map<String, String> users) {
    return (login, password) -> Objects.equals(users.get(login), password);
  }
}
