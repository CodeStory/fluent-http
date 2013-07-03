package net.codestory.http.templating;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Template {
  private final String url;

  public Template(String url) {
    this.url = url;
  }

  public String render() {
    return render(Collections.emptyMap());
  }

  public String render(String key, String value) {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put(key, value);
    return render(keyValues);
  }

  public String render(String key1, String value1, String key2, String value2) {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put(key1, value1);
    keyValues.put(key2, value2);
    return render(keyValues);
  }

  public String render(Map<String, String> keyValues) {
    String content = readContent(url);

    for (Map.Entry<String, String> keyValue : keyValues.entrySet()) {
      String key = keyValue.getKey();
      String value = keyValue.getValue();

      content = content.replace("[[" + key + "]]", value);
    }

    return content;
  }

  private String readContent(String url) {
    if (url.startsWith("classpath:")) {
      return readResource(url.substring(10));
    }
    if (url.startsWith("file:")) {
      return readFile(url.substring(5));
    }

    throw new IllegalArgumentException("Invalid path for static content. Should be prefixed by file: or classpath:");
  }

  private static String readResource(String url) {
    InputStream input = ClassLoader.getSystemResourceAsStream(url);
    if (input == null) {
      throw new IllegalArgumentException("Invalid url " + url);
    }

    try (Reader in = new BufferedReader(new InputStreamReader(input))) {
      StringBuilder buffer = new StringBuilder();

      int c;
      while ((c = in.read()) != -1) {
        buffer.append((char) c);
      }

      return buffer.toString();
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read " + url, e);
    }
  }

  private static String readFile(String url) {
    try {
      return new String(Files.readAllBytes(Paths.get(url)), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read " + url, e);
    }
  }
}
