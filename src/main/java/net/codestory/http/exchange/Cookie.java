package net.codestory.http.exchange;

public interface Cookie {
  String name();

  String value();

  int version();

  boolean isSecure();

  boolean isProtected();

  int expiry();

  String path();

  String domain();
}


