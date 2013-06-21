package net.codestory.http;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.types.*;

import com.google.gson.*;
import com.sun.net.httpserver.*;

public class Payload {
  final String contentType;
  final byte[] data;

  public Payload(String contentType, byte[] data) {
    this.contentType = contentType;
    this.data = data;
  }

  public Payload(Object content) {
    Payload payloadFromContent = create(content);
    this.contentType = payloadFromContent.contentType;
    this.data = payloadFromContent.data;
  }

  public Payload(String contentType, Object content) {
    Payload payloadFromContent = create(content);
    this.contentType = contentType;
    this.data = payloadFromContent.data;
  }

  public void writeTo(HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", contentType);
    exchange.sendResponseHeaders(200, data.length);
    exchange.getResponseBody().write(data);
  }

  private static Payload create(Object content) {
    if (content instanceof Payload) {
      return (Payload) content;
    }

    if (content instanceof byte[]) {
      return new Payload("application/octet-stream", (byte[]) content);
    }

    if (content instanceof String) {
      return new Payload("text/html", forString((String) content));
    }

    if (content instanceof Path) {
      Path path = (Path) content;
      try {
        return new Payload(new ContentTypes().get(path.toString()), Files.readAllBytes(path));
      } catch (IOException e) {
        return new Payload("text/plain", "BUG");
      }
    }

    if (content instanceof File) {
      File file = (File) content;
      try {
        return new Payload(new ContentTypes().get(file.getName()), Files.readAllBytes(file.toPath()));
      } catch (IOException e) {
        return new Payload("text/plain", "BUG");
      }
    }

    return new Payload("application/json", forString(new Gson().toJson(content)));
  }

  private static byte[] forString(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }
}
