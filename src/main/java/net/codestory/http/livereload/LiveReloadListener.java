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
import static net.codestory.http.livereload.LiveReload.VERSION_7;

import java.io.IOException;

import net.codestory.http.livereload.messages.IncomingHelloMessage;
import net.codestory.http.livereload.messages.OutgoingHelloMessage;
import net.codestory.http.livereload.messages.OutgoingReloadMessage;
import net.codestory.http.misc.Env;
import net.codestory.http.reload.MultiFolderWatcher;
import net.codestory.http.websockets.Frame;
import net.codestory.http.websockets.WebSocketListener;
import net.codestory.http.websockets.WebSocketSession;

public class LiveReloadListener implements WebSocketListener {
  private final WebSocketSession session;
  private final MultiFolderWatcher watcher;

  public LiveReloadListener(WebSocketSession session, Env env) {
    this.session = session;
    this.watcher = new MultiFolderWatcher(env.foldersToWatch(), () -> {
      try {
        session.send(TEXT, new OutgoingReloadMessage("path", true));
      } catch (IOException e) {
        // Ignore
      }
    });
    watcher.ensureStarted();
  }

  @Override
  public void onFrame(Frame frame) throws IOException {
    if (frame.type().equals(TEXT)) {
      IncomingHelloMessage message = frame.as(IncomingHelloMessage.class);

      if (message.command.equals("hello")) {
        if (!message.protocols.contains(VERSION_7)) {
          close();
          return;
        }

        sendHello();
      }
    }
  }

  private void close() throws IOException {
    watcher.stop();
    session.close();
  }

  private void sendHello() throws IOException {
    session.send(TEXT, new OutgoingHelloMessage("Fluent-http", asList(VERSION_7)));
  }
}
