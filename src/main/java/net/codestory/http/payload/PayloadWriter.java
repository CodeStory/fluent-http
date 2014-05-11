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
import java.util.zip.*;

import net.codestory.http.compilers.*;
import net.codestory.http.convert.*;
import net.codestory.http.exchange.*;
import net.codestory.http.io.*;
import net.codestory.http.misc.*;
import net.codestory.http.templating.*;
import net.codestory.http.types.*;

public class PayloadWriter {
  private final Request request;
  private final Response response;

  public PayloadWriter(Request request, Response response) {
    this.request = request;
    this.response = response;
  }

  public void write(Payload payload) throws IOException {
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

    final String uri = request.uri();
    String type = getContentType(payload, uri);
    response.setValue(CONTENT_TYPE, type);
    response.setStatus(code);

    if (HEAD.equals(request.method()) || (code == 204) || (code == 304) || ((code >= 100) && (code < 200))) {
      return;
    }

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

    String acceptEncoding = request.header(ACCEPT_ENCODING);
    if ((acceptEncoding != null) && acceptEncoding.contains(GZIP) && Env.INSTANCE.prodMode() && !Env.INSTANCE.disableGzip()) {
      response.setValue(CONTENT_ENCODING, GZIP);

      GZIPOutputStream gzip = new GZIPOutputStream(response.outputStream());
      gzip.write(data);
      gzip.finish();
    } else {
      response.setContentLength(data.length);
      response.outputStream().write(data);
    }
  }

  private String etag(byte[] data) {
    return Md5.of(data);
  }

  String getContentType(Payload payload, String uri) {
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

  byte[] getData(Payload payload, String uri) throws IOException {
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

    return TypeConvert.toByteArray(content);
  }

  private long getLastModified(Payload payload) {
    Object content = payload.rawContent();
    if (content instanceof Path) {
      return ((Path) content).toFile().lastModified();
    }
    if (content instanceof File) {
      return ((File) content).lastModified();
    }
    if (content instanceof CacheEntry) {
      return ((CacheEntry) content).lastModified();
    }

    return -1;
  }

  private static byte[] forString(String value) {
    return value.getBytes(UTF_8);
  }

  private byte[] forInputStream(InputStream stream) throws IOException {
    return InputStreams.readBytes(stream);
  }

  private byte[] forModelAndView(ModelAndView modelAndView) {
    String view = modelAndView.view();

    Map<String, Object> keyValues = new HashMap<>();
    keyValues.putAll(modelAndView.model().keyValues());
    keyValues.put("cookies", request.cookies().keyValues());
    keyValues.put("site", Site.get());

    CacheEntry html = new Template(view).render(keyValues);

    return html.toBytes();
  }

  private byte[] forPath(Path path) throws IOException {
    if (ContentTypes.is_binary(path)) {
      return Resources.readBytes(path);
    }

    if (ContentTypes.support_templating(path)) {
      return forModelAndView(ModelAndView.of(Resources.toUnixString(path)));
    }

    String content = Resources.read(path, UTF_8);
    CacheEntry compiled = Compilers.INSTANCE.compile(path, content);
    return compiled.toBytes();
  }
}
