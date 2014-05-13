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

import org.simpleframework.http.core.*;
import org.simpleframework.transport.*;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.connect.*;

import javax.net.ssl.*;

public class SimpleServerWrapper implements HttpServerWrapper {
  private final Server server;

  public SimpleServerWrapper(Handler handler) throws IOException {
    this.server = new ContainerServer((req, resp) -> handler.handle(new SimpleRequest(req), new SimpleResponse(resp)));
  }

  @Override
  public void start(int port, SSLContext context, boolean authReq) throws IOException {
    SocketConnection socketConnection = new SocketConnection(authReq ? new AuthRequiredServer(server) : server);
    socketConnection.connect(new InetSocketAddress(port), context);
  }

  @Override
  public void stop() throws IOException {
    server.stop();
  }

  private static class AuthRequiredServer implements Server {
    private final Server delegate;

    AuthRequiredServer(Server delegate) {
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
