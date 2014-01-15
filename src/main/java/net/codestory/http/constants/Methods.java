package net.codestory.http.constants;

public abstract class Methods {
  private Methods() {
    // Do not allow subclassing
  }

  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String HEAD = "HEAD";
  public static final String OPTIONS = "OPTIONS";
  public static final String TRACE = "TRACE";
  public static final String CONNECT = "CONNECT";
}