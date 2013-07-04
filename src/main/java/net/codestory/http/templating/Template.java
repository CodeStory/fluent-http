package net.codestory.http.templating;

import java.io.*;
import java.util.*;

import com.github.mustachejava.*;

public class Template {
  private final String url;

  public Template(String url) {
    this.url = url;
  }

  public String render() {
    return render(Collections.emptyMap());
  }

  public String render(String key, Object value) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(key, value);
    return render(keyValues);
  }

  public String render(String key1, String value1, String key2, Object value2) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(key1, value1);
    keyValues.put(key2, value2);
    return render(keyValues);
  }

  public String render(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(key1, value1);
    keyValues.put(key2, value2);
    keyValues.put(key3, value3);
    return render(keyValues);
  }

  public String render(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(key1, value1);
    keyValues.put(key2, value2);
    keyValues.put(key3, value3);
    keyValues.put(key4, value4);
    return render(keyValues);
  }

  public String render(Map<String, Object> keyValues) {
    DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();

    try (Reader reader = read(url)) {
      Mustache mustache = mustacheFactory.compile(reader, "", "[[", "]]");

      Writer output = new StringWriter();
      mustache.execute(output, keyValues).flush();
      return output.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to render template", e);
    }
  }

  private Reader read(String url) throws IOException {
    if (url.startsWith("classpath:")) {
      return readResource(url.substring(10));
    }
    if (url.startsWith("file:")) {
      return readFile(url.substring(5));
    }

    throw new IllegalArgumentException("Invalid path for static content. Should be prefixed by file: or classpath:");
  }

  private static Reader readResource(String url) {
    InputStream input = ClassLoader.getSystemResourceAsStream(url);
    if (input == null) {
      throw new IllegalArgumentException("Invalid url " + url);
    }
    return new InputStreamReader(input);
  }

  private static Reader readFile(String url) throws IOException {
    return new FileReader(url);
  }
}
