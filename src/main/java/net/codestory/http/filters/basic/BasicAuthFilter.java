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
package net.codestory.http.filters.basic;

import static net.codestory.http.constants.Headers.*;

import java.io.*;
import java.util.*;

import net.codestory.http.filters.*;
import net.codestory.http.internal.*;
import net.codestory.http.payload.*;

import org.simpleframework.http.parse.*;

public class BasicAuthFilter implements Filter {
  private final String uriPrefix;
  private final String realm;
  private final Users users;

  public BasicAuthFilter(String uriPrefix, String realm, Users users) {
    this.uriPrefix = uriPrefix;
    this.realm = realm;
    this.users = users;
  }

  public BasicAuthFilter(String uriPrefix, String realm, Map<String, String> users) {
    this(uriPrefix, realm, (login, password) -> Objects.equals(users.get(login), password));
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    if (!uri.startsWith(uriPrefix)) {
      return nextFilter.get(); // Ignore
    }

    String authorizationHeader = context.getHeader(AUTHORIZATION);
    if (authorizationHeader == null) {
      return Payload.unauthorized(realm);
    }

    PrincipalParser parser = new PrincipalParser(authorizationHeader);
    String login = parser.getName();
    String password = parser.getPassword();
    if (!users.isValid(login, password)) {
      return Payload.unauthorized(realm);
    }

    context.setCurrentUser(login);

    return nextFilter.get();
  }
}
