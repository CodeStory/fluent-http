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
package net.codestory.http.internal;

import java.io.*;
import java.net.*;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.*;
import org.simpleframework.transport.*;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.connect.*;

import javax.net.ssl.*;

public class SimpleServerWrapper implements HttpServerWrapper, Container {
  private final Handler handler;
  private final int count;
  private final int select;

  private SocketConnection socketConnection;

  public SimpleServerWrapper(Handler handler) {
    this(handler, 8, 1);
  }

  public SimpleServerWrapper(Handler handler, int count, int select) {
    this.handler = handler;
    this.count = count;
    this.select = select;
  }

  @Override
  public void start(int port, SSLContext context, boolean authReq) throws IOException {
    ContainerSocketProcessor server = new ContainerSocketProcessor(this, count, select);
    socketConnection = new SocketConnection(authReq ? new AuthRequiredServer(server) : server);
    socketConnection.connect(new InetSocketAddress(port), context);
  }

  @Override
  public void handle(Request request, Response response) {
    handler.handle(new SimpleRequest(request), new SimpleResponse(response));
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
