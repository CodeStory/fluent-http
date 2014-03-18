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
package net.codestory.http.security;

import java.io.*;

public interface User extends Serializable {
  String login();

  String[] roles();

  static User forLogin(String login) {
    return new User() {
      @Override
      public String login() {
        return login;
      }

      @Override
      public String[] roles() {
        return new String[0];
      }
    };
  }
}
