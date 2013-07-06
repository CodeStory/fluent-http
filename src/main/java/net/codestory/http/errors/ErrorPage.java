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

import static java.nio.charset.StandardCharsets.*;

import java.io.*;

import net.codestory.http.io.*;

import com.sun.net.httpserver.*;

public class ErrorPage {
  private final int code;
  private final Exception e;

  public ErrorPage(int code) {
    this(code, null);
  }

  public ErrorPage(int code, Exception e) {
    this.code = code;
    this.e = e;
  }

  public void writeTo(HttpExchange exchange) {
    try {
      byte[] data = readToString().getBytes(UTF_8);

      exchange.sendResponseHeaders(code, data.length);
      exchange.getResponseBody().write(data);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private String readToString() throws IOException {
    String content;
    if (code == 404) {
      content = Resources.toString("400.html", UTF_8);
    }else {
      content = Resources.toString("500.html", UTF_8);
    }
    
    return content.replace("[[ERROR]]", exceptionToString(e));
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