package net.codestory.http.exchange;

import java.io.*;
import java.net.*;
import java.util.*;

public interface HttpRequest {
  String uri();

  String method();

  String header(String name);

  String content() throws IOException;

  InputStream inputStream() throws IOException;

  List<String> headers(String name);

  InetSocketAddress clientAddress();

  boolean isSecure();

  Cookies cookies();

  HttpQuery query();

  <T> T unwrap(Class<T> type);
}