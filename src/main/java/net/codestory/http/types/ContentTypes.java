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

public class ContentTypes {
  public String get(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      return "text/plain";
    }

    String ext = filename.substring(dotIndex);
    switch (ext) {
      case ".txt":
        return "text/plain";
      case ".html":
        return "text/html";
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

  public boolean support_templating(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      return false;
    }

    String ext = filename.substring(dotIndex);
    switch (ext) {
      case ".txt":
      case ".html":
      case ".css":
      case ".less":
      case ".js":
      case ".coffee":
        return true;
      default:
        return false;
    }
  }
}
