package net.codestory.http.exchange;

import java.util.*;

import org.simpleframework.http.*;

// TODO: Hide Simple implementation
public interface Cookies extends Iterable<Cookie> {
  List<Cookie> list();

  Cookie get(String name);

  String value(String name);

  Map<String, String> keyValues();

  <T> T value(String name, T defaultValue);

  <T> T value(String name, Class<T> type);

  String value(String name, String defaultValue);

  int value(String name, int defaultValue);

  long value(String name, long defaultValue);

  boolean value(String name, boolean defaultValue);
}
