/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ClasspathScanner {
  private static final String DOT_CLASS = ".class";

  public Set<String> getResources(String prefix) {
    return listPaths(prefix, path -> !path.endsWith(DOT_CLASS));
  }

  public Set<Class<?>> getTypesAnnotatedWith(String packageToScan, Class<? extends Annotation> annotation) {
    Set<Class<?>> classes = new LinkedHashSet<>();
    try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages(packageToScan).scan()) {
      ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(annotation);
      classInfoList.stream().map(ClassInfo::loadClass).forEach(classes::add);
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
      Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(name);
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
