package net.codestory.http.exchange;

import java.io.*;
import java.net.*;
import java.util.*;

import org.simpleframework.transport.*;

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

  // TODO: Hide Simple implementation
  Certificate clientCertificate();

  <T> T unwrap(Class<T> type);
}