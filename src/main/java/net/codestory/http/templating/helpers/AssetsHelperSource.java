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
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Cache;
import net.codestory.http.misc.Sha1;
import org.webjars.WebJarAssetLocator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public class AssetsHelperSource {
  private final Resources resources;
  private final CompilerFacade compilers;
  private final Function<String, String> urlSupplier;
  private final WebJarAssetLocator webJarAssetLocator;

  public AssetsHelperSource(boolean prodMode, Resources resources, CompilerFacade compilers) {
    this.resources = resources;
    this.compilers = compilers;
    if (prodMode) {
      this.urlSupplier = new Cache<>(p -> uriWithSha1(p));
    } else {
      this.urlSupplier = path -> uriWithSha1(path);
    }
    this.webJarAssetLocator = new WebJarAssetLocator();
  }

  public CharSequence script(Object context) {
    return toString(context, value -> singleScript(value.toString()));
  }

  public CharSequence css(Object context) {
    return toString(context, value -> singleCss(value.toString()));
  }

  public CharSequence webjar(Object context) {
    return toString(context, value -> singleWebjar(value.toString()));
  }

  private static CharSequence toString(Object context, Function<Object, CharSequence> transform) {
    return new SafeString(contextAsList(context).stream().map(transform).collect(joining("\n")));
  }

  private static List<Object> contextAsList(Object context) {
    List<Object> list = new ArrayList<>();

    if (context instanceof Iterable<?>) {
      for (Object value : (Iterable<?>) context) {
        list.add(value);
      }
    } else {
      list.add(context);
    }

    return list;
  }

  private CharSequence singleScript(Object context) {
    String uri = addExtensionIfMissing(context.toString(), ".js");

    return "<script src=\"" + urlSupplier.apply(uri) + "\"></script>";
  }

  private CharSequence singleCss(Object context) {
    String uri = addExtensionIfMissing(context.toString(), ".css");

    return "<link rel=\"stylesheet\" href=\"" + urlSupplier.apply(uri) + "\">";
  }

  private CharSequence singleWebjar(Object context) {
    String uri = context.toString();

    String fullPath;
    try {
      fullPath = webJarAssetLocator.getFullPath(uri).replace("META-INF/resources/webjars/", "/webjars/");
    } catch (IllegalArgumentException e) {
      fullPath = uri;
    }

    if (fullPath.endsWith(".css")) {
      return "<link rel=\"stylesheet\" href=\"" + fullPath + "\">";
    } else {
      return "<script src=\"" + fullPath + "\"></script>";
    }
  }

  private static String addExtensionIfMissing(String uri, String extension) {
    return uri.endsWith(extension) ? uri : uri + extension;
  }

  private String uriWithSha1(String uri) {
    try {
      Path path = resources.findExistingPath(uri);
      if ((path != null) && (resources.isPublic(path))) {
        return uri + '?' + Sha1.of(resources.readBytes(path));
      }

      Path sourcePath = compilers.findPublicSourceFor(uri);
      if (sourcePath != null) {
        return uri + '?' + Sha1.of(resources.readBytes(sourcePath));
      }
    } catch (IOException e) {
      throw new IllegalStateException("Unable to compute sha1 for: " + uri, e);
    }

    return uri;
  }
}
