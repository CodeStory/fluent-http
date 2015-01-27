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

import static java.util.stream.Stream.of;

import java.io.Serializable;

public interface User extends Serializable {
  default String name() {
    return login();
  }

  String login();

  String[] roles();

  default boolean isInRole(String expectedRole) {
    return of(roles()).anyMatch(expectedRole::equals);
  }

  static User forLogin(String login) {
    return forLoginAndRoles(login);
  }

  static User forLoginAndRoles(String login, String... roles) {
    return new User() {
      @Override
      public String name() {
        return login;
      }

      @Override
      public String login() {
        return login;
      }

      @Override
      public String[] roles() {
        return roles;
      }
    };
  }
}
