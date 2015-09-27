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
package net.codestory.http.filters.auth;

import static org.assertj.core.api.Assertions.*;

import net.codestory.http.convert.*;

import org.junit.*;

public class AuthDataTest {
  @Test
  public void serialize_deserialize() {
    AuthData authData = new AuthData();
    authData.login = "johndoe";
    authData.roles = new String[]{"role1", "role2"};
    authData.redirectAfterLogin = "/url";
    authData.sessionId = "SESSION";

    String json = TypeConvert.toJson(authData);
    AuthData clone = TypeConvert.fromJson(json, AuthData.class);

    assertThat(clone).isEqualToComparingFieldByField(authData);
  }
}