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
package net.codestory.http.payload;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;
import static net.codestory.http.constants.Encodings.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.HttpStatus.*;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.io.Strings.*;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
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
  protected final Resources resources;
  protected final CompilerFacade compilers;

  public PayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers) {
    this.request = request;
    this.response = response;
    this.env = env;
    this.site = site;
    this.resources = resources;
    this.compilers = compilers;
  }

  public void writeAndClose(Payload payload) throws IOException {
    write(payload);
    if (!isStream(payload.rawContent())) {
      close();
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
    response.setHeaders(payload.headers());
    response.setCookies(payload.cookies());

    long lastModified = getLastModified(payload);
    if (lastModified >= 0) {
      String previousLastModified = stripQuotes(request.header(IF_MODIFIED_SINCE));
      if ((previousLastModified != null) && (lastModified < Dates.parseRfc1123(previousLastModified))) {
        response.setStatus(NOT_MODIFIED);
        return;
      }
      response.setHeader(LAST_MODIFIED, Dates.toRfc1123(lastModified));
    }

    int code = payload.code();
    String contentType = payload.rawContentType();
    Object content = payload.rawContent();
    if (content == null) {
      response.setStatus(code);
      response.setContentLength(0);
      return;
    }

    String uri = request.uri();

    String contentTypeHeader = (contentType != null) ? contentType : getContentType(content, uri);
    response.setHeader(CONTENT_TYPE, contentTypeHeader);
    response.setStatus(code);

    if (HEAD.equals(request.method()) || (code == NO_CONTENT) || (code == NOT_MODIFIED) || ((code >= 100) && (code < OK))) {
      return;
    }

    if (isStream(content)) {
      streamPayload(uri, payload);
    } else {
      writeBytes(uri, payload);
    }
  }

  protected void writeBytes(String uri, Payload payload) throws IOException {
    DataSupplier lazyData = DataSupplier.cache(() -> getData(payload.rawContent(), uri));

    String etag = payload.headers().get(ETAG);
    if (etag == null) {
      etag = etag(lazyData.get());
    }

    String previousEtag = stripQuotes(request.header(IF_NONE_MATCH));
    if (etag.equals(previousEtag)) {
      response.setStatus(NOT_MODIFIED);
      return;
    }
    response.setHeader(ETAG, etag);

    byte[] data = lazyData.get();
    write(data);
  }

  protected void writeStreamingHeaders() throws IOException {
    response.setHeader(CACHE_CONTROL, "no-cache");
    response.setHeader(CONNECTION, "keep-alive");
  }

  protected void streamPayload(String uri, Payload payload) throws IOException {
    writeStreamingHeaders();

    if (payload.rawContent() instanceof Stream<?>) {
      writeEventStream(payload);
    } else if (payload.rawContent() instanceof BufferedReader) {
      writeBufferedReader(payload);
    } else if (payload.rawContent() instanceof InputStream) {
      writeInputStream(payload);
    }
  }

  protected void writeEventStream(Payload payload) throws IOException {
    PrintStream printStream = new PrintStream(response.outputStream());

    try (Stream<?> stream = (Stream<?>) payload.rawContent()) {
      stream.forEach(item -> {
        String jsonOrPlainString = (item instanceof String) ? (String) item : TypeConvert.toJson(item);

        printStream
          .append("data: ")
          .append(jsonOrPlainString.replaceAll("[\n]", "\ndata: "))
          .append("\n\n")
          .flush();
      });
    }

    close();
  }

  protected void writeBufferedReader(Payload payload) throws IOException {
    BufferedReader lines = (BufferedReader) payload.rawContent();

    try (PrintStream outputStream = new PrintStream(response.outputStream(), true)) {
      String line;
      while (null != (line = lines.readLine())) {
        outputStream.println(line);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Unable to stream", e);
    }
  }

  protected void writeInputStream(Payload payload) throws IOException {
    InputStream stream = (InputStream) payload.rawContent();

    OutputStream outputStream = response.outputStream();

    try {
      InputStreams.copy(stream, outputStream);
      close();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to stream", e);
    }
  }

  protected void write(byte[] data) throws IOException {
    try {
      if (shouldGzip()) {
        response.setHeader(CONTENT_ENCODING, GZIP);

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
    return env.gzip() && env.prodMode() && request.header(ACCEPT_ENCODING, "").contains(GZIP);
  }

  protected boolean shouldIgnoreError(IOException e) {
    Throwable cause = e.getCause();
    if (cause != null) {
      String message = cause.getMessage();
      if ((message != null) && (message.contains("Connection reset by peer") || message.contains("Broken pipe"))) {
        return true;
      }
    }
    return false;
  }

  protected String etag(byte[] data) {
    return Md5.of(data);
  }

  protected boolean isStream(Object content) {
    return (content instanceof Stream<?>) || (content instanceof BufferedReader) || (content instanceof InputStream);
  }

  protected String getContentType(Object content, String uri) {
    if (content instanceof File) {
      File file = (File) content;
      return ContentTypes.get(file.getName());
    }
    if (content instanceof Path) {
      Path path = (Path) content;
      return ContentTypes.get(path.toString());
    }
    if (content instanceof SourceFile) {
      SourceFile sourceFile = (SourceFile) content;
      return ContentTypes.get(sourceFile.getPath().toString());
    }
    if (content instanceof URL) {
      return ContentTypes.get(((URL) content).getFile());
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
    if (content instanceof BufferedReader) {
      return "text/plain;charset=UTF-8";
    }
    if (content instanceof Stream<?>) {
      return "text/event-stream;charset=UTF-8";
    }
    if (content instanceof ModelAndView) {
      Path path = resources.findExistingPath(((ModelAndView) content).view());
      requireNonNull(path, "View not found for " + uri);
      return ContentTypes.get(path.toString());
    }
    if (content instanceof Model) {
      Path path = resources.findExistingPath(uri);
      requireNonNull(path, "View not found for " + uri);
      return ContentTypes.get(path.toString());
    }
    return "application/json;charset=UTF-8";
  }

  protected byte[] getData(Object content, String uri) throws IOException {
    if (content == null) {
      return null;
    }
    if (content instanceof File) {
      return forPath(((File) content).toPath());
    }
    if (content instanceof Path) {
      return forPath((Path) content);
    }
    if (content instanceof SourceFile) {
      return forSourceFile((SourceFile) content);
    }
    if (content instanceof URL) {
      return forURL((URL) content);
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
    String lastModified = payload.headers().get(LAST_MODIFIED);
    if (lastModified != null) {
      return Dates.parseRfc1123(lastModified);
    }

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

  protected byte[] forModelAndView(ModelAndView modelAndView) {
    Map<String, Object> keyValues = new HashMap<>();
    keyValues.putAll(modelAndView.model().keyValues());
    keyValues.put("cookies", request.cookies().keyValues());
    keyValues.put("env", env);
    keyValues.put("site", site);
    keyValues.put("request", request);
    keyValues.put("response", response);

    String body = compilers.renderView(modelAndView.view(), keyValues);
    return forString(body);
  }

  protected byte[] forURL(URL url) throws IOException {
    try (InputStream stream = url.openStream()) {
      return InputStreams.readBytes(stream);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read url:" + url, e);
    }
  }

  protected byte[] forPath(Path path) throws IOException {
    if (ContentTypes.supportsTemplating(path.toString())) {
      return forTemplatePath(path);
    }

    return resources.readBytes(path);
  }

  protected byte[] forSourceFile(SourceFile sourceFile) throws IOException {
    Path path = sourceFile.getPath();
    if (ContentTypes.supportsTemplating(path.toString())) {
      return forTemplatePath(path);
    }

    return compilers.compile(sourceFile).toBytes();
  }

  protected byte[] forTemplatePath(Path path) {
    return forModelAndView(ModelAndView.of(Resources.toUnixString(path)));
  }
}
