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

import java.io.IOException;
import java.util.Map;

import net.codestory.http.websockets.WebSocketListener;
import net.codestory.http.websockets.WebSocketSession;

import org.simpleframework.http.socket.CloseCode;
import org.simpleframework.http.socket.DataFrame;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.FrameType;
import org.simpleframework.http.socket.Reason;
import org.simpleframework.http.socket.Session;

class SimpleWebSocketSession implements WebSocketSession, Unwrappable {
  private final Session session;

  SimpleWebSocketSession(Session session) {
    this.session = session;
  }

  private FrameChannel channel() {
    return session.getChannel();
  }

  @Override
  public void remove(WebSocketListener listener) {
    throw new UnsupportedOperationException("remove");
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return session.getAttributes();
  }

  @Override
  public Object getAttribute(Object key) {
    return session.getAttribute(key);
  }

  @Override
  public void register(WebSocketListener listener) throws IOException {
    channel().register(new SimpleWebSocketListener(listener));
  }

  @Override
  public void send(String type, String message) throws IOException {
    channel().send(new DataFrame(FrameType.valueOf(type), message));
  }

  @Override
  public void send(String type, byte[] message) throws IOException {
    channel().send(new DataFrame(FrameType.valueOf(type), message));
  }

  @Override
  public void close() throws IOException {
    channel().close();
  }

  @Override
  public void close(String code, String reason) throws IOException {
    channel().close(new Reason(CloseCode.valueOf(code), reason));
  }

  @Override
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(session) ? (T) session : null;
  }
}