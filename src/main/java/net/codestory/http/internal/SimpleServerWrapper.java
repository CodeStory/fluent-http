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
package net.codestory.http.internal;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.codestory.http.websockets.WebSocketHandler;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.DirectRouter;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.http.socket.service.Service;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.SocketConnection;

import javax.net.ssl.SSLContext;

public class SimpleServerWrapper implements HttpServerWrapper, Container, Service {
  private final Handler httpHandler;
  private final WebSocketHandler webSocketHandler;
  private final int count;
  private final int select;

  private SocketConnection socketConnection;

  public SimpleServerWrapper(Handler httpHandler, WebSocketHandler webSocketHandler) {
    this(httpHandler, webSocketHandler, 8, 1);
  }

  public SimpleServerWrapper(Handler httpHandler, WebSocketHandler webSocketHandler, int count, int select) {
    this.httpHandler = httpHandler;
    this.webSocketHandler = webSocketHandler;
    this.count = count;
    this.select = select;
  }

  @Override
  public void start(int port, SSLContext context, boolean authReq) throws IOException {
    DirectRouter router = new DirectRouter(this);
    RouterContainer routerContainer = new RouterContainer(this, router, 10);
    ContainerSocketProcessor server = new ContainerSocketProcessor(routerContainer, count, select);
    socketConnection = new SocketConnection(authReq ? new AuthRequiredServer(server) : server);
    socketConnection.connect(new InetSocketAddress(port), context);
  }

  @Override
  public void handle(Request request, Response response) {
    httpHandler.handle(createRequest(request), createResponse(response));
  }

  @Override
  public void connect(Session session) {
    SimpleWebSocketSession webSocketSession = new SimpleWebSocketSession(session);
    SimpleRequest request = createRequest(session.getRequest());
    SimpleResponse response = createResponse(session.getResponse());

    webSocketHandler.connect(webSocketSession, request, response);
  }

  protected SimpleRequest createRequest(Request request) {
    return new SimpleRequest(request);
  }

  protected SimpleResponse createResponse(Response response) {
    return new SimpleResponse(response);
  }

  @Override
  public void stop() throws IOException {
    socketConnection.close();
  }

  private static class AuthRequiredServer implements SocketProcessor {
    private final SocketProcessor delegate;

    AuthRequiredServer(SocketProcessor delegate) {
      this.delegate = delegate;
    }

    @Override
    public void process(Socket socket) throws IOException {
      socket.getEngine().setNeedClientAuth(true);
      delegate.process(socket);
    }

    @Override
    public void stop() throws IOException {
      delegate.stop();
    }
  }
}
