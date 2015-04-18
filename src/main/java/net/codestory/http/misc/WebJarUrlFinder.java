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
package net.codestory.http.misc;

import net.codestory.http.io.ClassPaths;
import org.webjars.WebJarAssetLocator;

import java.net.URL;

public class WebJarUrlFinder {
  private final WebJarAssetLocator webJarAssetLocator;
  private final boolean useMinifiedVersions;

  public WebJarUrlFinder(boolean useMinifiedVersions) {
    this.webJarAssetLocator = new WebJarAssetLocator();
    this.useMinifiedVersions = useMinifiedVersions;
  }

  public URL url(String path) {
    URL urlMinified = getResource(minified(path));
    URL urlNotMinified = getResource(notMinified(path));

    if (useMinifiedVersions) {
      return (urlMinified != null) ? urlMinified : urlNotMinified;
    } else {
      return (urlNotMinified != null) ? urlNotMinified : urlMinified;
    }
  }

  private URL getResource(String uri) {
    try {
      String fullPath = webJarAssetLocator.getFullPath(uri);
      return ClassPaths.getResource(fullPath);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static String minified(String path) {
    return path.contains(".min.") ? path : path.replace(".js", ".min.js").replace(".css", ".min.css");
  }

  private static String notMinified(String path) {
    return path.replace(".min.", ".");
  }
}
