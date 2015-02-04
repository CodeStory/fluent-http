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
package net.codestory.http.livereload;

import static java.util.Arrays.asList;
import static net.codestory.http.constants.FrameTypes.TEXT;

import java.io.IOException;

import net.codestory.http.misc.Env;
import net.codestory.http.reload.FolderChangeListener;
import net.codestory.http.websockets.Frame;
import net.codestory.http.websockets.WebSocketListener;
import net.codestory.http.websockets.WebSocketSession;

public class LiveReloadListener implements WebSocketListener {
  public static final String VERSION_7 = "http://livereload.com/protocols/official-7";

  private final Env env;
  private final WebSocketSession session;
  private final FolderChangeListener listener;

  public LiveReloadListener(WebSocketSession session, Env env) {
    this.env = env;
    this.session = session;
    this.listener = () -> {
      try {
        session.send(TEXT, new OutgoingReloadMessage("path", true));
      } catch (IOException e) {
        // Ignore
      }
    };

    env.folderWatcher().addListener(listener);
  }

  @Override
  public void onFrame(Frame frame) throws IOException {
    if (frame.type().equals(TEXT)) {
      IncomingHelloMessage message = frame.as(IncomingHelloMessage.class);

      if ("hello".equals(message.command)) {
        if (message.protocols.contains(VERSION_7)) {
          session.send(TEXT, new OutgoingHelloMessage("Fluent-http", asList(VERSION_7)));
        } else {
          close();
        }
      }
    }
  }

  @Override
  public void onClose(int code, String reason) throws IOException {
    close();
  }

  private void close() throws IOException {
    env.folderWatcher().removeListener(listener);
    session.close();
  }
}
