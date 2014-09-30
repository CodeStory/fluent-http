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

import static java.util.stream.Stream.*;
import static net.codestory.http.constants.Headers.*;
import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.payload.Payload.*;

import java.io.*;
import java.util.concurrent.*;

import net.codestory.http.*;
import net.codestory.http.convert.*;
import net.codestory.http.filters.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.security.*;

import org.apache.commons.lang3.*;

public class JsonCookieAuthFilter implements Filter {
  private static final int ONE_DAY = (int) TimeUnit.DAYS.toSeconds(1L);
  private static final String[] DEFAULT_EXCLUDE = {".less", ".css", ".map", ".js", ".coffee", ".ico", ".jpeg", ".jpg", ".gif", ".png", ".svg", ".eot", ".ttf", ".woff", ".js", ".coffee", "robots.txt"};

  private final String uriPrefix;
  private final Users users;
  private final SessionIdStore sessionIdStore;
  private final String[] ignoreExtensions;

  public JsonCookieAuthFilter(String uriPrefix, Users users) {
    this(uriPrefix, users, SessionIdStore.inMemory(), DEFAULT_EXCLUDE);
  }

  public JsonCookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore) {
    this(uriPrefix, users, sessionIdStore, DEFAULT_EXCLUDE);
  }

  public JsonCookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore, String ignoreExtension, String... moreIgnoreExtensions) {
    this(uriPrefix, users, sessionIdStore, Fluent.of(ignoreExtension).concat(moreIgnoreExtensions).toArray(String[]::new));
  }

  private JsonCookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore, String[] ignoreExtensions) {
    this.uriPrefix = uriPrefix;
    this.users = users;
    this.sessionIdStore = sessionIdStore;
    this.ignoreExtensions = ignoreExtensions;
  }

  @Override
  public boolean matches(String uri, Context context) {
    return uri.startsWith("/auth/") || (uri.startsWith(uriPrefix) && of(ignoreExtensions).noneMatch(uri::endsWith));
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    return uri.startsWith("/auth/") ? authenticationUri(uri, context, nextFilter) : otherUri(uri, context, nextFilter);
  }

  private Payload authenticationUri(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    String method = context.method();

    if (uri.equals("/auth/signin") && method.equals(POST)) {
      return signin(context);
    }

    if (uri.equals("/auth/signout") && method.equals(GET)) {
      return signout(context);
    }

    return nextFilter.get(); // Don't protect other /auth/ pages. Login and lost password pages for eg.
  }

  private Payload otherUri(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    String sessionId = readSessionIdInCookie(context);
    if (sessionId != null) {
      String login = sessionIdStore.getLogin(sessionId);
      if (login != null) {
        User user = users.find(login);
        context.setCurrentUser(user);
        return nextFilter.get().withHeader(CACHE_CONTROL, "must-revalidate");
      }
    }

    return seeOther("/auth/login")
      .withCookie(authCookie(null));
  }

  private Payload signin(Context context) {
    String login = context.get("login");
    String password = context.get("password");

    User user = users.find(login, password);
    if (user == null) {
      return seeOther("/auth/login"); // Unknown user. Go back to login
    }

    return seeOther(notFavIcon(readRedirectUrlInCookie(context)))
      .withCookie(authCookie(buildCookie(user, "/")));
  }

  private Payload signout(Context context) {
    String sessionId = context.cookies().value("sessionId");
    if (sessionId != null) {
      sessionIdStore.remove(sessionId);
    }

    return seeOther("/?signout")
      .withCookie(authCookie(null));
  }

  private String readSessionIdInCookie(Context context) {
    AuthData authData = context.cookies().value("auth", AuthData.class);
    return (authData == null) ? null : authData.sessionId;
  }

  private String readRedirectUrlInCookie(Context context) {
    AuthData authData = context.cookies().value("auth", AuthData.class);
    String redirectUrl = (authData == null) ? null : authData.redirectAfterLogin;
    redirectUrl = (redirectUrl == null) ? "/" : redirectUrl;
    return redirectUrl;
  }

  private String newSessionId(String login) {
    String sessionId = RandomStringUtils.random(32, true, true);
    sessionIdStore.put(sessionId, login);
    return sessionId;
  }

  private String buildCookie(User user, String redirectUrl) {
    AuthData cookie = new AuthData();
    cookie.login = user.login();
    cookie.roles = user.roles();
    cookie.sessionId = newSessionId(user.login());
    cookie.redirectAfterLogin = redirectUrl;

    return TypeConvert.toJson(cookie);
  }

  private static Cookie authCookie(String authData) {
    NewCookie cookie = new NewCookie("auth", authData, "/", true);
    cookie.setExpiry(ONE_DAY);
    cookie.setDomain(null);
    cookie.setSecure(false);
    return cookie;
  }

  private static String notFavIcon(String redirectUrl) {
    return redirectUrl.contains("favicon.ico") ? "/" : redirectUrl;
  }
}