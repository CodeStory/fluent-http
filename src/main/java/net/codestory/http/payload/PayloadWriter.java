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
package net.codestory.http.payload;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;
import static net.codestory.http.constants.Encodings.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.HttpStatus.*;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.io.Strings.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.zip.*;

import net.codestory.http.*;
import net.codestory.http.compilers.*;
import net.codestory.http.convert.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

public class PayloadWriter {
  protected final Request request;
  protected final Response response;
  protected final Env env;
  protected final Site site;
  protected final CompilerFacade compilers;
  protected final ExecutorService executorService;

  public PayloadWriter(Request request, Response response, Env env, Site site, CompilerFacade compilers, ExecutorService executorService) {
    this.request = request;
    this.response = response;
    this.env = env;
    this.site = site;
    this.compilers = compilers;
    this.executorService = executorService;
  }

  public void writeAndClose(Payload payload) throws IOException {
    try {
      write(payload);
    } finally {
      if (!isStream(payload)) {
        close();
      }
    }
  }

  protected void close() {
    try {
      response.close();
    } catch (IOException e) {
      // Ignore
    }
  }

  protected void write(Payload payload) throws IOException {
    payload.headers().forEach(response::setValue);
    payload.cookies().forEach(response::setCookie);

    long lastModified = getLastModified(payload);
    if (lastModified >= 0) {
      String previousLastModified = stripQuotes(request.header(IF_MODIFIED_SINCE));
      if ((previousLastModified != null) && (lastModified < Dates.parse_rfc_1123(previousLastModified))) {
        response.setStatus(NOT_MODIFIED);
        return;
      }
      response.setValue(LAST_MODIFIED, Dates.to_rfc_1123(lastModified));
    }

    int code = payload.code();
    Object content = payload.rawContent();
    if (content == null) {
      response.setStatus(code);
      response.setContentLength(0);
      return;
    }

    String uri = request.uri();
    String type = getContentType(payload, uri);
    response.setValue(CONTENT_TYPE, type);
    response.setStatus(code);

    if (HEAD.equals(request.method()) || (code == 204) || (code == 304) || ((code >= 100) && (code < 200))) {
      return;
    }

    if (isStream(payload)) {
      streamPayload(uri, payload);
    } else {
      writeBytes(uri, payload);
    }
  }

  protected void writeBytes(String uri, Payload payload) throws IOException {
    DataSupplier lazyData = DataSupplier.cache(() -> getData(payload, uri));

    String etag = payload.headers().get(ETAG);
    if (etag == null) {
      etag = etag(lazyData.get());
    }

    String previousEtag = stripQuotes(request.header(IF_NONE_MATCH));
    if (etag.equals(previousEtag)) {
      response.setStatus(NOT_MODIFIED);
      return;
    }
    response.setValue(ETAG, etag);

    byte[] data = lazyData.get();
    write(data);
  }

  protected void streamPayload(String uri, Payload payload) throws IOException {
    response.setValue(CACHE_CONTROL, "no-cache");
    response.setValue(CONNECTION, "keep-alive");

    if (payload.rawContent() instanceof Stream<?>) {
      Stream<?> stream = (Stream<?>) payload.rawContent();

      PrintStream printStream = response.printStream();

      executorService.submit(() -> {
        stream.forEach(item -> {
          String json = (item instanceof String) ? (String) item : TypeConvert.toJson(item);

          printStream
              .append("data: ")
              .append(json)
              .append("\n\n")
              .flush();
        });
        close();
      });
    } else if (payload.rawContent() instanceof InputStream) {
      InputStream stream = (InputStream) payload.rawContent();

      OutputStream outputStream = response.outputStream();

      executorService.submit(() -> {
        try {
          InputStreams.copy(stream, outputStream);
          close();
        } catch (IOException e) {
          throw new IllegalStateException("Unable to stream", e);
        }
      });
    }
  }

  protected void write(byte[] data) throws IOException {
    try {
      if (shouldGzip()) {
        response.setValue(CONTENT_ENCODING, GZIP);

        GZIPOutputStream gzip = new GZIPOutputStream(response.outputStream());
        gzip.write(data);
        gzip.finish();
      } else {
        response.setContentLength(data.length);
        response.outputStream().write(data);
      }
    } catch (IOException e) {
      if (!shouldIgnoreError(e)) {
        throw e;
      }
    }
  }

  protected boolean shouldGzip() {
    if (env.disableGzip() || !env.prodMode()) {
      return false;
    }
    return request.header(ACCEPT_ENCODING, "").contains(GZIP);
  }

  protected boolean shouldIgnoreError(IOException e) {
    Throwable cause = e.getCause();
    if (cause != null) {
      String message = cause.getMessage();
      if (message != null) {
        if (message.contains("Connection reset by peer") || message.contains("Broken pipe")) {
          return true;
        }
      }
    }
    return false;
  }

  protected String etag(byte[] data) {
    return Md5.of(data);
  }

  protected boolean isStream(Payload payload) {
    Object content = payload.rawContent();
    if (content instanceof Stream<?>) {
      return true;
    }
    if (content instanceof InputStream) {
      return true;
    }
    return false;
  }

  protected String getContentType(Payload payload, String uri) {
    String contentType = payload.rawContentType();
    if (contentType != null) {
      return contentType;
    }

    Object content = payload.rawContent();
    if (content instanceof File) {
      File file = (File) content;
      return ContentTypes.get(file.toPath());
    }
    if (content instanceof Path) {
      Path path = (Path) content;
      return ContentTypes.get(path);
    }
    if (content instanceof CompiledPath) {
      CompiledPath compiledPath = (CompiledPath) content;
      return ContentTypes.get(compiledPath.getReponsePath());
    }
    if (content instanceof byte[]) {
      return "application/octet-stream";
    }
    if (content instanceof String) {
      return "text/html;charset=UTF-8";
    }
    if (content instanceof CacheEntry) {
      return "text/html;charset=UTF-8";
    }
    if (content instanceof InputStream) {
      return "application/octet-stream";
    }
    if (content instanceof Stream<?>) {
      return "text/event-stream";
    }
    if (content instanceof ModelAndView) {
      Path path = Resources.findExistingPath(((ModelAndView) content).view());
      requireNonNull(path, "View not found for " + uri);
      return ContentTypes.get(path);
    }
    if (content instanceof Model) {
      Path path = Resources.findExistingPath(uri);
      requireNonNull(path, "View not found for " + uri);
      return ContentTypes.get(path);
    }
    return "application/json;charset=UTF-8";
  }

  protected byte[] getData(Payload payload, String uri) throws IOException {
    Object content = payload.rawContent();
    if (content == null) {
      return null;
    }
    if (content instanceof File) {
      return forPath(((File) content).toPath());
    }
    if (content instanceof Path) {
      return forPath((Path) content);
    }
    if (content instanceof CompiledPath) {
      return forCompiledPath((CompiledPath) content);
    }
    if (content instanceof byte[]) {
      return (byte[]) content;
    }
    if (content instanceof String) {
      return forString((String) content);
    }
    if (content instanceof CacheEntry) {
      return ((CacheEntry) content).toBytes();
    }
    if (content instanceof InputStream) {
      return forInputStream((InputStream) content);
    }
    if (content instanceof ModelAndView) {
      return forModelAndView((ModelAndView) content);
    }
    if (content instanceof Model) {
      return forModelAndView(ModelAndView.of(uri, (Model) content));
    }

    return toJson(content);
  }

  protected byte[] toJson(Object content) {
    return TypeConvert.toByteArray(content);
  }

  protected long getLastModified(Payload payload) throws IOException {
    Object content = payload.rawContent();
    if (content instanceof File) {
      return ((File) content).lastModified();
    }
    if (content instanceof Path) {
      return ((Path) content).toFile().lastModified();
    }

    return -1;
  }

  protected byte[] forString(String value) {
    return value.getBytes(UTF_8);
  }

  protected byte[] forInputStream(InputStream stream) throws IOException {
    return InputStreams.readBytes(stream);
  }

  protected byte[] forModelAndView(ModelAndView modelAndView) {
    String view = modelAndView.view();

    Map<String, Object> keyValues = new HashMap<>();
    keyValues.putAll(modelAndView.model().keyValues());
    keyValues.put("cookies", request.cookies().keyValues());
    keyValues.put("env", env);
    keyValues.put("site", site);

    CacheEntry html = new Template(view).render(keyValues, compilers);

    return html.toBytes();
  }

  protected byte[] forPath(Path path) throws IOException {
    if (ContentTypes.support_templating(path)) {
      return forTemplatePath(path);
    }

    return Resources.readBytes(path);
  }

  protected byte[] forCompiledPath(CompiledPath compiledPath) throws IOException {
    Path path = compiledPath.getSourcePath();
    if (ContentTypes.support_templating(path)) {
      return forTemplatePath(path);
    }

    String content = Resources.read(path, UTF_8);
    return compilers.compile(path, content).toBytes();
  }

  protected byte[] forTemplatePath(Path path) {
    return forModelAndView(ModelAndView.of(Resources.toUnixString(path)));
  }
}
