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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UsersList implements Users {
  private final Map<String, User> usersByLogin;
  private final Map<String, String> passwordsByLogin;

  private UsersList(Map<String, User> usersByLogin, Map<String, String> passwordsByLogin) {
    this.usersByLogin = usersByLogin;
    this.passwordsByLogin = passwordsByLogin;
  }

  @Override
  public User find(String login, String password) {
    if (Objects.equals(password, passwordsByLogin.get(login))) {
      return usersByLogin.get(login);
    }
    return null;
  }

  @Override
  public User find(String login) {
    return usersByLogin.get(login);
  }

  public static class Builder {
    private final Map<String, User> usersByLogin = new HashMap<>();
    private final Map<String, String> passwordsByLogin = new HashMap<>();

    public Builder addUser(String login, String password, String... roles) {
      usersByLogin.put(login, User.forLoginAndRoles(login, roles));
      passwordsByLogin.put(login, password);
      return this;
    }

    public UsersList build() {
      return new UsersList(usersByLogin, passwordsByLogin);
    }
  }
}
