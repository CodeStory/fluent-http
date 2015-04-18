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
package net.codestory.http.cors;

import net.codestory.http.Request;

import java.net.URI;
import java.net.URISyntaxException;

import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.cors.CORSRequestType.*;
import static net.codestory.http.types.ContentTypes.*;

public abstract class CORSHelper {
  private CORSHelper() {
    // Do not allow subclassing
  }

  public static CORSRequestType corsRequestType(Request request) {
    String origin = request.header(ORIGIN);
    if (origin == null) {
      return NOT_CORS;
    }

    if (isInvalidOrigin(origin)) {
      return INVALID_CORS;
    }

    switch (request.method()) {
      case OPTIONS:
        String accessControl = request.header(ACCESS_CONTROL_REQUEST_METHOD);
        if (accessControl == null) {
          return ACTUAL;
        }
        return accessControl.isEmpty() ? INVALID_CORS : PRE_FLIGHT;
      case GET:
      case HEAD:
        return SIMPLE;
      case POST:
        String contentType = request.contentType();
        if (contentType == null) {
          return INVALID_CORS;
        }
        return SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(contentType.toLowerCase().trim()) ? SIMPLE : ACTUAL;
      case PUT:
      case DELETE:
      case TRACE:
      case CONNECT:
        return ACTUAL;
      default:
        return INVALID_CORS;
    }
  }

  private static boolean isInvalidOrigin(String origin) {
    if (origin.isEmpty() || origin.contains("%")) {
      return true;
    }
    try {
      return new URI(origin).getScheme() == null;
    } catch (URISyntaxException e) {
      return true;
    }
  }
}
