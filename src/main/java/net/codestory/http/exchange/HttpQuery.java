package net.codestory.http.exchange;

import java.util.*;

public interface HttpQuery {
  String get(String name);

  List<String> all(String name);

  int getInteger(String name);

  float getFloat(String name);

  boolean getBoolean(String name);

  Map<String, String> keyValues();

  <T> T unwrap(Class<T> type);
}
