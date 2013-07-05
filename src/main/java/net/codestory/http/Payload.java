package net.codestory.http;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.types.*;

import com.google.gson.*;
import com.sun.net.httpserver.*;

public class Payload {
  private final String contentType;
  private final Object content;
  private final int code;
  private final Map<String, String> responseHeaders;

  public Payload(Object content) {
    this(null, content);
  }

  public Payload(String contentType, Object content) {
    this(contentType, content, 200);
  }

  public Payload(String contentType, Object content, int code) {
    this.contentType = contentType;
    this.content = content;
    this.code = code;
    responseHeaders = new HashMap<>();
  }

  public static Payload wrap(Object payload) {
    if (payload instanceof Payload) {
      return (Payload) payload;
    }
    return new Payload(payload);
  }

  public static Payload seeOther(String url) {
    Payload payload = new Payload(null, null, 303);
    payload.responseHeaders.put("Location", url);
    return payload;
  }

  public int code() {
    return code;
  }

  public void writeTo(HttpExchange exchange) throws IOException {
    for (Map.Entry<String, String> keyValue : responseHeaders.entrySet()) {
      exchange.getResponseHeaders().add(keyValue.getKey(), keyValue.getValue());
    }

    byte[] data = getData();
    if (data != null) {
      exchange.getResponseHeaders().add("Content-Type", getContentType());

      exchange.sendResponseHeaders(code, data.length);
      exchange.getResponseBody().write(data);
    } else {
      exchange.sendResponseHeaders(code, 0);
    }
  }

  String getContentType() {
    if (contentType != null) {
      return contentType;
    }
    if (content instanceof File) {
      File file = (File) content;
      return new ContentTypes().get(file.getName());
    }
    if (content instanceof Path) {
      Path path = (Path) content;
      return new ContentTypes().get(path.toString());
    }
    if (content instanceof byte[]) {
      return "application/octet-stream";
    }
    if (content instanceof InputStream) {
      return "application/octet-stream";
    }
    if (content instanceof String) {
      return "text/html";
    }
    return "application/json";
  }

  byte[] getData() throws IOException {
    if (content == null) {
      return null;
    }
    if (content instanceof File) {
      return forPath(((File) content).toPath());
    }
    if (content instanceof Path) {
      return forPath((Path) content);
    }
    if (content instanceof byte[]) {
      return (byte[]) content;
    }
    if (content instanceof String) {
      return forString((String) content);
    }
    if (content instanceof InputStream) {
      return forInputStream((InputStream) content);
    }
    return forString(new Gson().toJson(content));
  }

  private static byte[] forPath(Path path) throws IOException {
    if (path.toString().endsWith(".less")) {
      return forString(new LessCompiler().compile(path));
    }
    if (path.toString().endsWith(".coffee")) {
      return forString(new CoffeeScriptCompiler().compile(path));
    }
    return Files.readAllBytes(path);
  }

  private static byte[] forString(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] forInputStream(InputStream stream) throws IOException {
    return Bytes.readBytes(stream);
  }
}
