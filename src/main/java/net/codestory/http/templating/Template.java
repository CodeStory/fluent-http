/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
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

  public String render(String k1, String v1, String k2, Object v2) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    return render(keyValues);
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    keyValues.put(k3, v3);
    return render(keyValues);
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.put(k1, v1);
    keyValues.put(k2, v2);
    keyValues.put(k3, v3);
    keyValues.put(k4, v4);
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
