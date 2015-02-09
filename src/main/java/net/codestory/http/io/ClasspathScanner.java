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
import java.lang.annotation.Annotation;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

import static net.codestory.http.io.Strings.replaceLast;

public class ClasspathScanner {
  public Set<String> getResources(Path root) {
    String prefix = root.toString();

    return listPaths(prefix, path -> !path.endsWith(".class"));
  }

  public Set<Class<?>> getTypesAnnotatedWith(String packageToScan, Class<? extends Annotation> annotation) {
    String prefix = packageToScan.replace('.', '/');

    Set<Class<?>> classes = new LinkedHashSet<>();

    for (String classFile : listPaths(prefix, path -> path.endsWith(".class"))) {
      String className = replaceLast(classFile.replace('/', '.'), ".class", "");

      try {
        Class<?> type = Class.forName(className);
        if (type.isAnnotationPresent(annotation)) {
          classes.add(type);
        }
      } catch (Exception e) {
        // Ignore
      }
    }

    return classes;
  }

  public Set<String> listPaths(String prefix, Predicate<String> filter) {
    Set<String> paths = new LinkedHashSet<>();

    for (URL url : urls(prefix)) {
      for (String rawPath : ClassPaths.fromURL(url)) {
        String path = rawPath.replace('\\', '/');
        if (path.startsWith(prefix) && filter.test(path)) {
          paths.add(path);
        }
      }
    }

    return paths;
  }

  private static Set<URL> urls(String name) {
    Set<URL> result = new LinkedHashSet<>();

    try {
      Enumeration<URL> urls = ClasspathScanner.class.getClassLoader().getResources(name);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        String externalForm = url.toExternalForm().replace('\\', '/');
        int index = externalForm.lastIndexOf(name);
        if (index != -1) {
          result.add(new URL(externalForm.substring(0, index)));
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
