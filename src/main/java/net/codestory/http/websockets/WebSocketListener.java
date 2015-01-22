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
package net.codestory.http.websockets;

import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface WebSocketListener {
  void onFrame(WebSocketSession session, String type, Supplier<String> textSupplier) throws IOException;

  default void onError(WebSocketSession session, Exception cause) throws IOException {
    // Do nothing
  }

  default void onClose(WebSocketSession session, int code, String reason) throws IOException {
    // Do nothing
  }
}