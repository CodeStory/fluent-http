package net.codestory.http.exchange;

public interface Cookie {
  String name();

  String value();

  int version();

  boolean isNew();

  boolean isSecure();

  boolean isProtected();

  int expiry();

  String path();

  String domain();

  <T> T unwrap(Class<T> type);
}


