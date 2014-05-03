package net.codestory.http.exchange;

import java.io.*;

public interface HttpResponse {
  void close() throws IOException;

  OutputStream outputStream() throws IOException;

  void setContentLength(long length);

  void setValue(String name, String value);

  void setStatus(int statusCode);

  void setCookie(NewCookie cookie);
}
