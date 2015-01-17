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

import java.io.*;
import java.net.*;

import net.codestory.http.websockets.*;

import org.simpleframework.http.*;
import org.simpleframework.http.core.*;
import org.simpleframework.http.socket.*;
import org.simpleframework.http.socket.service.*;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.*;
import org.simpleframework.transport.connect.*;

import javax.net.ssl.*;

public class SimpleServerWrapper implements HttpServerWrapper, Container {
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
    Router negotiator = new DirectRouter(session -> {
      try {
        SimpleRequest request = new SimpleRequest(session.getRequest());
        SimpleResponse response = new SimpleResponse(session.getResponse());
        WebSocketListener delegate = webSocketHandler.create(request, response);

        session.getChannel().register(new FrameListener() {
          @Override
          public void onFrame(Session session, Frame frame) {
            FrameType type = frame.getType();

            if (!type.isPing() && !type.isPong()) {
              delegate.onFrame(new SimpleWebSocketSession(session), request, response, type.name(), () -> frame.getText());
            }
          }

          @Override
          public void onError(Session session, Exception cause) {
            delegate.onError(new SimpleWebSocketSession(session), request, response, cause);
          }

          @Override
          public void onClose(Session session, Reason reason) {
            delegate.onClose(new SimpleWebSocketSession(session), request, response, reason.getCode().code, reason.getText());
          }
        });
      } catch (IOException e) {
        throw new RuntimeException("WebSocket error", e);
      }
    });

    RouterContainer webSocketRouter = new RouterContainer(this, negotiator, 10);
    ContainerSocketProcessor server = new ContainerSocketProcessor(webSocketRouter, count, select);
    socketConnection = new SocketConnection(authReq ? new AuthRequiredServer(server) : server);
    socketConnection.connect(new InetSocketAddress(port), context);
  }

  @Override
  public void handle(Request request, Response response) {
    httpHandler.handle(new SimpleRequest(request), new SimpleResponse(response));
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
