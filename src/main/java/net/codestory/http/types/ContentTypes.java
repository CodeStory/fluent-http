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

import static net.codestory.http.io.Resources.*;

import java.nio.file.*;

public class ContentTypes {
  public static final String[] TEMPLATE_EXTENSIONS = {"", ".html", ".md", ".markdown", ".txt", ".asciidoc"};

  private ContentTypes() {
    // Static class
  }

  public static String get(Path path) {
    switch (extension(path)) {
      case ".html":
      case ".md":
      case ".markdown":
      case ".asciidoc":
      case ".adoc":
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

  public static boolean support_templating(Path path) {
    switch (extension(path)) {
      case ".txt":
      case ".md":
      case ".markdown":
      case ".asciidoc":
      case ".adoc":
      case ".html":
      case ".xml":
      case ".css":
      case ".less":
      case ".map":
        return true;
      default:
        return false;
    }
  }

  public static boolean is_binary(Path path) {
    switch (extension(path)) {
      case ".txt":
      case ".md":
      case ".markdown":
      case ".asciidoc":
      case ".adoc":
      case ".html":
      case ".xml":
      case ".css":
      case ".less":
      case ".map":
      case ".js":
      case ".coffee":
      case ".litcoffee":
      case ".svg":
        return false;
      default:
        return true;
    }
  }
}
