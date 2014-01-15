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

import static java.time.ZonedDateTime.*;
import static java.time.format.DateTimeFormatter.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.Methods.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import net.codestory.http.internal.*;
import net.codestory.http.io.*;
import net.codestory.http.payload.*;
import net.codestory.http.types.*;

class StaticRoute implements Route {
  protected static final Path NOT_FOUND = Paths.get("");

  @Override
  public Payload apply(String uri, Context context) throws IOException {
    if (uri.startsWith("/webjars/")) {
      return serverFromWebjar(uri, context);
    }
    return serveFromApp(uri, context);
  }

  private Payload serverFromWebjar(String uri, Context context) throws IOException {
    URL classpathUrl = ClassLoader.getSystemResource("META-INF/resources" + uri);
    if (classpathUrl == null) {
      return Payload.notFound();
    }

    if (!GET.equalsIgnoreCase(context.method()) && !HEAD.equalsIgnoreCase(context.method())) {
      return Payload.methodNotAllowed();
    }

    if (context.getHeader(IF_MODIFIED_SINCE) != null) {
      return Payload.notModified();
    }

    try (InputStream stream = classpathUrl.openStream()) {
      String contentType = ContentTypes.get(Paths.get(uri));
      byte[] data = InputStreams.readBytes(stream);

      return new Payload(contentType, data)
          .withHeader(CACHE_CONTROL, "public, max-age=31536000")
          .withHeader(LAST_MODIFIED, RFC_1123_DATE_TIME.format(now().minusMonths(1L)))
          .withHeader(EXPIRES, RFC_1123_DATE_TIME.format(now().plusWeeks(1L)));
    }
  }

  private Payload serveFromApp(String uri, Context context) {
    Path path = path(uri);
    if (path == NOT_FOUND) {
      if (uri.endsWith("/") || (path(uri + "/") == NOT_FOUND)) {
        return Payload.notFound();
      }
      return Payload.seeOther(uri + "/");
    }

    if (!GET.equalsIgnoreCase(context.method()) && !HEAD.equalsIgnoreCase(context.method())) {
      return Payload.methodNotAllowed();
    }

    return new Payload(path);
  }

  protected Path path(String uri) {
    Path path = Resources.findExistingPath(uri);
    return (path != null) && Resources.isPublic(path) ? path : NOT_FOUND;
  }
}
