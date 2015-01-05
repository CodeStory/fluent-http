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
package net.codestory.http.templating.helpers;

import com.github.jknack.handlebars.Handlebars.SafeString;
import net.codestory.http.compilers.CompilerFacade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.lang.String.join;

public class AssetsHelperSource {
  private final CompilerFacade compilers;
  private final Map<Path, String> sha1Cache = new ConcurrentHashMap<>();
  private final Function<Path, String> sha1Supplier;

  public AssetsHelperSource(boolean prodMode, CompilerFacade compilers) {
    this.compilers = compilers;
    if (prodMode) {
      this.sha1Supplier = path -> sha1Cache.computeIfAbsent(path, p -> sha1(p));
    } else {
      this.sha1Supplier = path -> sha1(path);
    }
  }

  public CharSequence script(Object context) throws IOException {
    List<CharSequence> scripts = new ArrayList<>();

    if (context instanceof Iterable<?>) {
      for (Object value : (Iterable<?>) context) {
        scripts.add(singleScript(value));
      }
    } else {
      scripts.add(singleScript(context));
    }

    return new SafeString(join("\n", scripts));
  }

  public CharSequence css(Object context) throws IOException {
    List<CharSequence> scripts = new ArrayList<>();

    if (context instanceof Iterable<?>) {
      for (Object value : (Iterable<?>) context) {
        scripts.add(singleCss(value));
      }
    } else {
      scripts.add(singleCss(context));
    }

    return new SafeString(join("\n", scripts));
  }

  private CharSequence singleScript(Object context) throws IOException {
    String uri = addExtensionIfMissing(context.toString(), ".js");

    return "<script src=\"" + uriWithSha1(uri) + "\"></script>";
  }

  private CharSequence singleCss(Object context) throws IOException {
    String uri = addExtensionIfMissing(context.toString(), ".css");

    return "<link rel=\"stylesheet\" href=\"" + uriWithSha1(uri) + "\">";
  }

  private static String addExtensionIfMissing(String uri, String extension) {
    return uri.endsWith(extension) ? uri : uri + extension;
  }

  private String uriWithSha1(String uri) throws IOException {
    Path path = compilers.findPublicSourceFor(uri);
    return (path == null) ? uri : uri + '?' + sha1Supplier.apply(path);
  }

  private String sha1(Path path) {
    try {
      return compilers.compile(path).sha1();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to compute sha1 for: " + path);
    }
  }
}
