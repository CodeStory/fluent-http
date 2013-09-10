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
package net.codestory.http.types;

import java.nio.file.*;

public class ContentTypes {
  private ContentTypes() {
    // Static class
  }

  public static String get(Path path) {
    switch (extension(path)) {
      case ".html":
      case ".md":
        return "text/html";
      case ".xml":
        return "application/xml";
      case ".css":
      case ".less":
        return "text/css";
      case ".js":
      case ".coffee":
        return "application/javascript";
      case ".zip":
        return "application/zip";
      case ".gif":
        return "image/gif";
      case ".jpeg":
      case ".jpg":
        return "image/jpeg";
      case ".png":
        return "image/png";
      default:
        return "text/plain";
    }
  }

  public static boolean support_templating(Path path) {
    switch (extension(path)) {
      case ".txt":
      case ".md":
      case ".html":
      case ".xml":
      case ".css":
      case ".less":
        return true;
      default:
        return false;
    }
  }

  public static boolean is_binary(Path path) {
    switch (extension(path)) {
      case ".txt":
      case ".md":
      case ".html":
      case ".xml":
      case ".css":
      case ".less":
      case ".js":
      case ".coffee":
        return false;
      default:
        return true;
    }
  }

  private static String extension(Path path) {
    String filename = path.toString();
    int dotIndex = filename.lastIndexOf('.');
    return dotIndex == -1 ? "" : filename.substring(dotIndex);
  }
}
