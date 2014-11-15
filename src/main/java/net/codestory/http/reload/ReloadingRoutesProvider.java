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
package net.codestory.http.reload;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.codestory.http.*;
import net.codestory.http.compilers.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.routes.*;

import org.slf4j.*;

class ReloadingRoutesProvider implements RoutesProvider {
  private final static Logger LOG = LoggerFactory.getLogger(ReloadingRoutesProvider.class);

  private final Env env;
  private final Supplier<CompilerFacade> compiler;
  private final Configuration configuration;
  private final AtomicBoolean dirty;

  private List<FolderWatcher> classesWatchers;
  private FolderWatcher appWatcher;

  private RouteCollection routes;

  ReloadingRoutesProvider(Env env, Supplier<CompilerFacade> compiler, Configuration configuration) {
    this.env = env;
    this.compiler = compiler;
    this.configuration = configuration;
    this.dirty = new AtomicBoolean(true);
  }

  protected Stream<Path> classpathFolders() {
    URL[] urls = ClassPaths.getUrls(Thread.currentThread().getContextClassLoader());
    return of(urls).map(url -> Paths.get(toUri(url)));
  }

  private static URI toUri(URL url) {
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Unable to convert URL to URI: " + url, e);
    }
  }

  @Override
  public synchronized RouteCollection get() {
    if (dirty.get()) {
      LOG.info("Reloading configuration...");

      if (classesWatchers == null) {
        this.classesWatchers = classpathFolders().map(path -> new FolderWatcher(path, ev -> dirty.set(true))).collect(toList());
      }
      classesWatchers.forEach(FolderWatcher::ensureStarted);

      if (appWatcher == null) {
        this.appWatcher = new FolderWatcher(env.appPath(), ev -> dirty.set(true));
      }
      appWatcher.ensureStarted();

      routes = new RouteCollection(env, compiler.get());
      configuration.configure(routes);
      routes.installExtensions();
      routes.addStaticRoutes(false);

      dirty.set(false);
    }

    return routes;
  }
}
