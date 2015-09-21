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
package net.codestory.http.internal;

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
import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleServerWrapper implements HttpServerWrapper, Container, Service {
  private final Handler httpHandler;
  private final WebSocketHandler webSocketHandler;
  private final int threadCount;
  private final int selectThreads;
  private final int webSocketThreads;

  private SocketConnection socketConnection;

  /**
   * @param threadCount     the number of threads used for each pool.
   * @param selectThreads    the number of selector threads to use.
   * @param webSocketThreads the number of threads to use in router selector.
   */
  public SimpleServerWrapper(Handler httpHandler, WebSocketHandler webSocketHandler, int threadCount, int selectThreads, int webSocketThreads) {
    this.httpHandler = httpHandler;
    this.webSocketHandler = webSocketHandler;
    this.threadCount = threadCount;
    this.selectThreads = selectThreads;
    this.webSocketThreads = webSocketThreads;
  }

  @Override
  public int start(int port, SSLContext context, boolean authReq) throws IOException {
    DirectRouter router = new DirectRouter(this);
    RouterContainer routerContainer = new RouterContainer(this, router, webSocketThreads);
    ContainerSocketProcessor processor = new ContainerSocketProcessor(routerContainer, threadCount, selectThreads);

    socketConnection = new SocketConnection(authReq ? new AuthRequiredServer(processor) : processor);

    InetSocketAddress actualAddress = (InetSocketAddress) socketConnection.connect(new InetSocketAddress(port), context);
    return actualAddress.getPort();
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
