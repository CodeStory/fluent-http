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
package net.codestory.http.filters.basic;

import static java.nio.charset.StandardCharsets.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.payload.Payload.*;

import java.util.*;

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

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
    this(uriPrefix, realm, Users.forMap(users));
  }

  @Override
  public boolean matches(String uri, Context context) {
    return uri.startsWith(uriPrefix);
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
    String authorization = context.header(AUTHORIZATION);
    if ((authorization == null) || (!authorization.toLowerCase(Locale.ENGLISH).startsWith("basic "))) {
      return unauthorized(realm);
    }

    String base64Pwd = authorization.substring("basic ".length());
    String auth = new String(Base64.getDecoder().decode(base64Pwd), UTF_8);

    int i = auth.indexOf(':');
    if (i < 0) {
      return Payload.badRequest();
    }

    String login = auth.substring(0, i);
    String password = auth.substring(i + 1);

    User user = users.find(login, password);
    if (user == null) {
      return unauthorized(realm);
    }

    context.setCurrentUser(user);

    return nextFilter.get().withHeader(CACHE_CONTROL, "must-revalidate");
  }
}
