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

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.io.*;

public class ErrorPage {
  private final int code;
  private final Exception exception;

  public ErrorPage(int code, Exception exception) {
    this.code = code;
    this.exception = exception;
  }

  public Payload payload() throws IOException {
    String html = readHtml().replace("[[ERROR]]", exceptionToString(exception));
    return new Payload("text/html", html, code);
  }

  private String readHtml() throws IOException {
    if (code == 404) {
      return Resources.read(Paths.get("classpath:400.html"), StandardCharsets.UTF_8);
    }
    return Resources.read(Paths.get("classpath:500.html"), StandardCharsets.UTF_8);
  }

  private static String exceptionToString(Exception error) {
    if (error == null) {
      return "";
    }

    StringWriter string = new StringWriter();
    try (PrintWriter message = new PrintWriter(string)) {
      error.printStackTrace(message);
    }
    return string.toString();
  }
}