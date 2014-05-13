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
package net.codestory.http.filters.mixed;

import static net.codestory.http.constants.Headers.*;

import java.io.*;

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.filters.auth.*;
import net.codestory.http.filters.basic.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

public class MixedAuthFilter implements Filter {
  private final Filter cookieAuthFilter;
  private final Filter basicAuthFilter;

  public MixedAuthFilter(String uriPrefix, String realm, Users users, SessionIdStore sessionIdStore) {
    this.cookieAuthFilter = new CookieAuthFilter(uriPrefix, users, sessionIdStore);
    this.basicAuthFilter = new BasicAuthFilter(uriPrefix, realm, users);
  }

  @Override
  public boolean matches(String uri, Context context) {
    return authFilter(context).matches(uri, context);
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    return authFilter(context).apply(uri, context, nextFilter);
  }

  private Filter authFilter(Context context) {
    return (context.header(AUTHORIZATION) == null) ? cookieAuthFilter : basicAuthFilter;
  }
}
