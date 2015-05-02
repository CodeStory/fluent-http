package net.codestory.http.servlet;

import com.google.common.collect.Lists;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import net.codestory.http.*;
import net.codestory.http.io.InputStreams;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.reload.RoutesProvider;
import net.codestory.http.routes.RouteCollection;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FluentHttpServletFilter implements ContainerResponseFilter {
  private final RoutesProvider routesProvider;

  public FluentHttpServletFilter(Configuration configuration) {
    this(configuration, Env.prod().withGzip(false));
  }

  public FluentHttpServletFilter(Configuration configuration, Env env) {
    this.routesProvider = RoutesProvider.fixed(env, configuration);
  }

  @Override
  public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
    handleHttp(new JerseyRequest(containerRequest), new JerseyResponse(containerResponse));
    return containerResponse;
  }

  private void handleHttp(Request request, Response response) {
    RouteCollection routes = routesProvider.get();

    PayloadWriter writer = routes.createPayloadWriter(request, response);
    try {
      Payload payload = routes.apply(request, response);

      writer.writeAndClose(payload);
    } catch (Exception e) {
      writer.writeErrorPage(e);
    }
  }

  static class JerseyRequest implements Request {
    private final ContainerRequest containerRequest;

    JerseyRequest(ContainerRequest containerRequest) {
      this.containerRequest = containerRequest;
    }

    @Override
    public String uri() {
      return containerRequest.getRequestUri().getPath();
    }

    @Override
    public String method() {
      return containerRequest.getMethod();
    }

    @Override
    public String content() throws IOException {
      // Is this the right encoding?
      return InputStreams.readString(inputStream(), UTF_8); // Could be a default method
    }

    @Override
    public String contentType() {
      return containerRequest.getMediaType().getType();
    }

    @Override
    // Could be a Collection or an Iterable in interface
    public List<String> headerNames() {
      return Lists.newArrayList(containerRequest.getRequestHeaders().keySet());
    }

    @Override
    public List<String> headers(String name) {
      return containerRequest.getRequestHeaders().get(name);
    }

    @Override
    public String header(String name) {
      return containerRequest.getHeaderValue(name);
    }

    @Override
    public InputStream inputStream() {
      return containerRequest.getEntityInputStream();
    }

    @Override
    public InetSocketAddress clientAddress() {
      // TODO
      return null;
    }

    @Override
    public boolean isSecure() {
      return containerRequest.isSecure();
    }

    @Override
    public Cookies cookies() {
      return new JerseyCookies(containerRequest);
    }

    @Override
    public Query query() {
      return new JerseyQuery(containerRequest);
    }

    @Override
    public List<Part> parts() {
      // TODO
      return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return type.isInstance(containerRequest) ? (T) containerRequest : null;
    }
  }

  static class JerseyQuery implements Query {
    private final ContainerRequest containerRequest;

    JerseyQuery(ContainerRequest containerRequest) {
      this.containerRequest = containerRequest;
    }

    @Override
    public Collection<String> keys() {
      // TODO: Strange
      Set<String> keys = new HashSet<>();

      MultivaluedMap<String, String> queryParameters = containerRequest.getQueryParameters();
      keys.addAll(queryParameters.keySet());

      try {
        Form formParameters = containerRequest.getFormParameters();
        keys.addAll(formParameters.keySet());
      } catch (Exception e) {
        // Ignore
      }

      return keys;
    }

    @Override
    public Iterable<String> all(String name) {
      // TODO: Strange
      List<String> all = new ArrayList<>();

      MultivaluedMap<String, String> queryParameters = containerRequest.getQueryParameters();
      if (queryParameters.get(name) != null) {
        all.addAll(queryParameters.get(name));
      }

      try {
        Form formParameters = containerRequest.getFormParameters();
        if (formParameters.get(name) != null) {
          all.addAll(formParameters.get(name));
        }
      } catch (Exception e) {
        // Ignore
      }

      return all;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return type.isInstance(containerRequest) ? (T) containerRequest : null;
    }
  }

  static class JerseyCookies implements Cookies {
    private final ContainerRequest containerRequest;
    private final Map<String, javax.ws.rs.core.Cookie> cookies;

    JerseyCookies(ContainerRequest containerRequest) {
      this.containerRequest = containerRequest;
      this.cookies = containerRequest.getCookies();
    }

    @Override
    public Cookie get(String name) {
      javax.ws.rs.core.Cookie cookie = cookies.get(name);
      return (cookie == null) ? null : new JerseyCookie(cookie);
    }

    @Override
    public Iterator<Cookie> iterator() {
      return cookies.values().stream().map(cookie -> (Cookie) new JerseyCookie(cookie)).iterator();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return type.isInstance(containerRequest) ? (T) containerRequest : null;
    }
  }

  static class JerseyCookie implements Cookie {
    private final javax.ws.rs.core.Cookie cookie;

    JerseyCookie(javax.ws.rs.core.Cookie cookie) {
      this.cookie = cookie;
    }

    @Override
    public String name() {
      return cookie.getName();
    }

    @Override
    public String value() {
      return cookie.getValue();
    }

    @Override
    public int version() {
      return cookie.getVersion();
    }

    @Override
    public boolean isNew() {
      return (cookie instanceof javax.ws.rs.core.NewCookie);
    }

    @Override
    public boolean isSecure() {
      return (cookie instanceof javax.ws.rs.core.NewCookie) && ((javax.ws.rs.core.NewCookie) cookie).isSecure();
    }

    @Override
    public boolean isHttpOnly() {
      // TODO
      return false;
    }

    @Override
    public int expiry() {
      if (!(cookie instanceof javax.ws.rs.core.NewCookie)) {
        return -1; // TODO
      }
      return ((javax.ws.rs.core.NewCookie) cookie).getMaxAge();
    }

    @Override
    public String path() {
      return cookie.getPath();
    }

    @Override
    public String domain() {
      return cookie.getDomain();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return type.isInstance(cookie) ? (T) cookie : null;
    }
  }

  static class JerseyResponse implements Response {
    private final ContainerResponse containerResponse;
    private long contentLength = -1;

    JerseyResponse(ContainerResponse containerResponse) {
      this.containerResponse = containerResponse;
    }

    @Override
    public void close() throws IOException {
      ContainerResponseWriter writer = containerResponse.getContainerResponseWriter();
      writer.writeStatusAndHeaders(contentLength, containerResponse);
      writer.finish();
    }

    @Override
    public OutputStream outputStream() throws IOException {
      return containerResponse.getOutputStream();
    }

    @Override
    public void setContentLength(long length) {
      this.contentLength = length;
    }

    @Override
    public void setHeader(String name, String value) {
      containerResponse.getHttpHeaders().add(name, value);
    }

    @Override
    public void setStatus(int statusCode) {
      containerResponse.setStatus(statusCode);
    }

    @Override
    public void setCookie(Cookie cookie) {
      javax.ws.rs.core.NewCookie newCookie = new javax.ws.rs.core.NewCookie(cookie.name(), cookie.value(), cookie.path(), cookie.domain(), "", cookie.expiry(), cookie.isSecure());
      javax.ws.rs.core.Response cookieResponse = javax.ws.rs.core.Response.fromResponse(containerResponse.getResponse()).cookie(newCookie).build();
      containerResponse.setResponse(cookieResponse);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return type.isInstance(containerResponse) ? (T) containerResponse : null;
    }
  }
}