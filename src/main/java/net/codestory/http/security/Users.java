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
package net.codestory.http.security;

import java.io.*;
import java.util.*;

import static java.util.Collections.singletonMap;

public interface Users extends Serializable {
  User find(String login, String password);

  User find(String login);

  static Users forMap(Map<String, String> users) {
    return new Users() {
      @Override
      public User find(String login, String password) {
        String userPassword = users.get(login);
        return Objects.equals(userPassword, password) ? User.forLogin(login) : null;
      }

      @Override
      public User find(String login) {
        return users.containsKey(login) ? User.forLogin(login) : null;
      }
    };
  }

  static Users singleUser(String login, String password) {
    return forMap(singletonMap(login, password));
  }
}
