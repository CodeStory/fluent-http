package net.codestory.http;

import static java.util.Arrays.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.types.*;

import org.jcoffeescript.*;

import com.github.sommeri.less4j.core.*;
import com.google.gson.*;
import com.sun.net.httpserver.*;

public class Payload {
  private final String contentType;
  private final Object content;

  public Payload(Object content) {
    this(null, content);
  }

  public Payload(String contentType, Object content) {
    this.contentType = contentType;
    this.content = content;
  }

  public void writeTo(HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", getContentType());

    byte[] data = getData();
    exchange.sendResponseHeaders(200, data.length);
    exchange.getResponseBody().write(data);
  }

  String getContentType() {
    if (contentType != null) {
      return contentType;
    }
    if (content instanceof Payload) {
      return ((Payload) content).getContentType();
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
    if (content instanceof String) {
      return "text/html";
    }
    return "application/json";
  }

  byte[] getData() throws IOException {
    if (content instanceof Payload) {
      return ((Payload) content).getData();
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
    return forString(new Gson().toJson(content));
  }

  private static byte[] forPath(Path path) throws IOException {
    if (path.toString().endsWith(".less")) {
      return forString(compileLess(path));
    }
    if (path.toString().endsWith(".coffee")) {
      return forString(compileCoffee(path));
    }
    return Files.readAllBytes(path);
  }

  private static String compileCoffee(Path path) throws IOException {
    try {
      String coffee = new String(Files.readAllBytes(path));

      return new JCoffeeScriptCompiler(asList(Option.BARE)).compile(coffee);
    } catch (Exception e) {
      throw new IOException("Unable to compile less file", e);
    }
  }

  private static String compileLess(Path path) throws IOException {
    try {
      return new ThreadUnsafeLessCompiler().compile(path.toFile()).getCss();
    } catch (Exception e) {
      throw new IOException("Unable to compile less file", e);
    }
  }

  private static byte[] forString(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }
}
