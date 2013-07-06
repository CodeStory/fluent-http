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
package net.codestory.http.routes;

import java.io.*;
import java.net.*;
import java.nio.file.*;

class StaticFileRoute extends StaticRoute {
  StaticFileRoute(String directoryPath) {
    super(toPath(directoryPath));
  }

  private static Path toPath(String directoryPath) {
    File rootResource = new File(directoryPath);
    if (!rootResource.exists()) {
      throw new IllegalArgumentException("Invalid directory for static content: " + directoryPath);
    }

    return Paths.get(directoryPath);
  }
}
