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
package net.codestory.http.errors;

import static net.codestory.http.constants.HttpStatus.*;

import java.io.*;

import net.codestory.http.payload.*;
import net.codestory.http.templating.*;

public class ErrorPage {
  private final Payload payload;
  private final Exception exception;

  public ErrorPage(Payload payload, Exception exception) {
    this.payload = payload;
    this.exception = exception;
  }

  public Payload payload() {
    String error = toString(exception);
    String filename = filename();

    return new Payload("text/html", ModelAndView.of(filename, "ERROR", error), payload.code())
      .withHeaders(payload.headers())
      .withCookies(payload.cookies());
  }

  private String filename() {
    return (payload.code() == NOT_FOUND) ? "404.html" : "500.html";
  }

  private static String toString(Exception error) {
    if (error == null) {
      return "";
    }

    Writer string = new StringWriter();
    try (PrintWriter message = new PrintWriter(string)) {
      error.printStackTrace(message);
    }
    return string.toString();
  }
}