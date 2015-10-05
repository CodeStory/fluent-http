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

import net.codestory.http.websockets.WebSocketListener;

import org.simpleframework.http.socket.Frame;
import org.simpleframework.http.socket.FrameListener;
import org.simpleframework.http.socket.FrameType;
import org.simpleframework.http.socket.Reason;
import org.simpleframework.http.socket.Session;

class SimpleWebSocketListener implements FrameListener {
  private final WebSocketListener listener;

  SimpleWebSocketListener(WebSocketListener listener) {
    this.listener = listener;
  }

  @Override
  public void onFrame(Session session, Frame frame) {
    FrameType type = frame.getType();
    if (!type.isPing() && !type.isPong()) {
      try {
        listener.onFrame(new SimpleFrame(frame));
      } catch (IOException e) {
        throw new IllegalStateException("Unable to handle frame", e);
      }
    }
  }

  @Override
  public void onError(Session session, Exception cause) {
    try {
      listener.onError(cause);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to handle error", e);
    }
  }

  @Override
  public void onClose(Session session, Reason reason) {
    try {
      listener.onClose(reason.getCode().code, reason.getText());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to handle close", e);
    }
  }
}