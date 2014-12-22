/**
 * Copyright (C) 2013-2014 all@code-story.net
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
package net.codestory.http.io;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class ClasspathScanner {
  public Set<String> getResources(Path root) {
    String prefix = root.toString();

    Set<String> resources = new LinkedHashSet<>();

    for (URL url : urls(prefix)) {
      for (String rawPath : ClassPaths.fromURL(url)) {
        String path = rawPath.replace('\\', '/');
        if (path.startsWith(prefix) && !path.endsWith(".class")) {
          resources.add(path);
        }
      }
    }

    return resources;
  }

  private static Set<URL> urls(String name) {
    Set<URL> result = new LinkedHashSet<>();

    try {
      Enumeration<URL> urls = ClasspathScanner.class.getClassLoader().getResources(name);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        int index = url.toExternalForm().lastIndexOf(name);
        if (index != -1) {
          result.add(new URL(url.toExternalForm().substring(0, index)));
        } else {
          result.add(url);
        }
      }
    } catch (IOException e) {
      // Ignore
    }

    return result;
  }
}
