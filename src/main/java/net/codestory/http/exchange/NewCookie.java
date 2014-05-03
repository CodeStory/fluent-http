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
package net.codestory.http.exchange;

import org.simpleframework.http.*;

// TODO: Hide Simple implementation
public class NewCookie extends Cookie {
  public NewCookie(String name, String value) {
    super(name, value, "/");
  }

  public NewCookie(String name, String value, boolean created) {
    super(name, value, "/", created);
  }

  public NewCookie(String name, String value, String path) {
    super(name, value, path, false);
  }

  public NewCookie(String name, String value, String path, boolean created) {
    super(name, value, path, created);
  }
}
