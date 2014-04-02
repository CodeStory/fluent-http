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
package net.codestory.http.misc;

import java.net.*;

public class WebJarUrlFinder {
  private final boolean useMinifiedVersions;

  public WebJarUrlFinder(boolean useMinifiedVersions) {
    this.useMinifiedVersions = useMinifiedVersions;
  }

  public URL url(String path) {
    URL urlMinified = getResource(minified(path));
    URL urlNonMinified = getResource(notMinified(path));

    if (useMinifiedVersions) {
      return (urlMinified != null) ? urlMinified : urlNonMinified;
    } else {
      return (urlNonMinified != null) ? urlNonMinified : urlMinified;
    }
  }

  private static URL getResource(String uri) {
    return ClassLoader.getSystemResource("META-INF/resources" + uri);
  }

  private static String minified(String path) {
    return path.contains(".min.") ? path : path.replace(".js", ".min.js").replace(".css", ".min.css");
  }

  private static String notMinified(String path) {
    return path.replace(".min.", ".");
  }
}
