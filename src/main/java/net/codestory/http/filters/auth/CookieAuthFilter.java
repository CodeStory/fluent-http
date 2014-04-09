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
package net.codestory.http.filters.auth;

import static net.codestory.http.constants.Methods.*;

import java.io.*;
import java.util.concurrent.*;

import net.codestory.http.filters.*;
import net.codestory.http.internal.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

import org.apache.commons.lang3.*;
import org.simpleframework.http.*;

public class CookieAuthFilter implements Filter {
  private static final int ONE_DAY = (int) TimeUnit.DAYS.toSeconds(1L);
  private static final String[] DEFAULT_EXCLUDE = {".less", ".css", ".map", ".ico", ".jpeg", ".jpg", ".gif", ".png", ".svg", ".eot", ".ttf", ".woff"};

  private final String uriPrefix;
  private final Users users;
  private final SessionIdStore sessionIdStore;
  private final String[] ignoreExtensions;

  public CookieAuthFilter(String uriPrefix, Users users) {
    this(uriPrefix, users, SessionIdStore.inMemory(), DEFAULT_EXCLUDE);
  }

  public CookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore, String... ignoreExtensions) {
    this.uriPrefix = uriPrefix;
    this.users = users;
    this.sessionIdStore = sessionIdStore;
    this.ignoreExtensions = ignoreExtensions;
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    if (!uri.startsWith(uriPrefix)) {
      return nextFilter.get(); // Ignore
    }
    for (String ignoreExtension : ignoreExtensions) {
      if (uri.endsWith(ignoreExtension)) {
        return nextFilter.get(); // Ignore
      }
    }

    String method = context.method();

    if (uri.equals("/auth/login") && method.equals(GET)) {
      return nextFilter.get(); // Login page
    }

    if (uri.equals("/auth/signin") && method.equals(POST)) {
      String login = context.get("login");
      String password = context.get("password");

      if (users.find(login, password) == null) {
        return Payload.seeOther("/auth/login.html");
      }

      String sessionId = create(login);

      return Payload.seeOther("/") // Should be the url asked by the user
          .withCookie(loginCookie(login))
          .withCookie(sessionCookie(sessionId));
    }

    if (uri.equals("/auth/signout") && method.equals(GET)) {
      String sessionId = context.cookieValue("sessionId", "");

      sessionIdStore.remove(sessionId);

      return Payload.seeOther("/?signout")
          .withCookie(loginCookie(null))
          .withCookie(sessionCookie(null));
    }

    if (uri.startsWith("/auth/")) {
      return nextFilter.get(); // Don't protect other /auth/ pages
    }

    String sessionId = context.cookieValue("sessionId");
    if (sessionId != null) {
      String login = sessionIdStore.getLogin(sessionId);
      if (login != null) {
        User user = users.find(login);
        context.setCurrentUser(user);
        return nextFilter.get();
      }
    }

    return Payload.seeOther("/auth/login")
        .withCookie(loginCookie(null))
        .withCookie(sessionCookie(null));
  }

  private String create(String login) {
    String sessionId = RandomStringUtils.random(32, true, true);
    sessionIdStore.put(sessionId, login);
    return sessionId;
  }

  private static Cookie loginCookie(String login) {
    return expire(new Cookie("login", login, "/", true));
  }

  private static Cookie sessionCookie(String sessionId) {
    return expire(new Cookie("sessionId", sessionId, "/", true));
  }

  private static Cookie expire(Cookie cookie) {
    cookie.setExpiry(ONE_DAY);
    cookie.setDomain(null);
    cookie.setSecure(false);
    return cookie;
  }
}