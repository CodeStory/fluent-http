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
package net.codestory.http.types;

import static java.util.Arrays.*;
import static net.codestory.http.io.Resources.*;

import java.nio.file.*;
import java.util.*;

public class ContentTypes {
  public static final String[] TEMPLATE_EXTENSIONS = {"", ".html", ".md", ".markdown", ".txt"};

  private ContentTypes() {
    // Static class
  }

  public static final List<String> SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES = asList(
    "application/x-www-form-urlencoded",
    "multipart/form-data",
    "text/plain"
  );

  private static String extension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex <= 0) ? "" : filename.substring(dotIndex);
  }

  public static String get(String filename) {
    switch (extension(filename)) {
      case ".html":
      case ".md":
      case ".markdown":
        return "text/html;charset=UTF-8";
      case ".xml":
        return "application/xml;charset=UTF-8";
      case ".css":
      case ".less":
        return "text/css;charset=UTF-8";
      case ".js":
      case ".coffee":
      case ".litcoffee":
        return "application/javascript;charset=UTF-8";
      case ".zip":
        return "application/zip";
      case ".gz":
        return "application/gzip";
      case ".pdf":
        return "application/pdf";
      case ".gif":
        return "image/gif";
      case ".jpeg":
      case ".jpg":
        return "image/jpeg";
      case ".png":
        return "image/png";
      case ".svg":
        return "image/svg+xml";
      case ".eot":
        return "application/vnd.ms-fontobject";
      case ".ttf":
        return "application/x-font-ttf";
      case ".woff":
        return "application/x-font-woff";
      case ".ico":
        return "image/x-icon";
      default:
        return "text/plain;charset=UTF-8";
    }
  }

  public static boolean supportsTemplating(String filename) {
    switch (extension(filename)) {
      case ".txt":
      case ".md":
      case ".markdown":
      case ".html":
      case ".xml":
        return true;
      default:
        return false;
    }
  }
}
