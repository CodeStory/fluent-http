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
package net.codestory.http.filters.auth;

import net.codestory.http.Context;
import net.codestory.http.Cookie;
import net.codestory.http.NewCookie;
import net.codestory.http.convert.TypeConvert;
import net.codestory.http.filters.Filter;
import net.codestory.http.filters.PayloadSupplier;
import net.codestory.http.payload.Payload;
import net.codestory.http.security.SessionIdStore;
import net.codestory.http.security.User;
import net.codestory.http.security.Users;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.toHexString;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static net.codestory.http.constants.Headers.CACHE_CONTROL;
import static net.codestory.http.constants.Methods.GET;
import static net.codestory.http.constants.Methods.POST;
import static net.codestory.http.payload.Payload.seeOther;
import static net.codestory.http.payload.Payload.unauthorized;

public class CookieAuthFilter implements Filter {
  public static final String[] DEFAULT_EXCLUDE = {".less", ".css", ".map", ".js", ".coffee", ".ico", ".jpeg", ".jpg", ".gif", ".png", ".svg", ".eot", ".ttf", ".woff", "woff2", "robots.txt"};

  protected static final Random RANDOM = new Random();
  protected static final int ONE_DAY = (int) TimeUnit.DAYS.toSeconds(1L);

  protected final String uriPrefix;
  protected final Users users;
  protected final SessionIdStore sessionIdStore;
  protected final String[] ignoreExtensions;

  public CookieAuthFilter(String uriPrefix, Users users) {
    this(uriPrefix, users, SessionIdStore.inMemory(), DEFAULT_EXCLUDE);
  }

  public CookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore) {
    this(uriPrefix, users, sessionIdStore, DEFAULT_EXCLUDE);
  }

  public CookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore, String ignoreExtension, String... moreIgnoreExtensions) {
    this(uriPrefix, users, sessionIdStore, concat(of(ignoreExtension), of(moreIgnoreExtensions)).toArray(String[]::new));
  }

  private CookieAuthFilter(String uriPrefix, Users users, SessionIdStore sessionIdStore, String[] ignoreExtensions) {
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
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
    return uri.startsWith("/auth/") ? authenticationUri(uri, context, nextFilter) : otherUri(uri, context, nextFilter);
  }

  protected Payload authenticationUri(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
    String method = context.method();

    if (uri.startsWith("/auth/signin") && POST.equals(method)) {
      return signin(context);
    }

    if (uri.startsWith("/auth/signout") && GET.equals(method)) {
      return signout(context);
    }

    // Don't protect other /auth/ pages. Login and lost password pages for eg.
    return nextFilter.get();
  }

  protected Payload otherUri(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
    String sessionId = readSessionIdInCookie(context);
    if (sessionId != null) {
      String login = sessionIdStore.getLogin(sessionId);
      if (login != null) {
        User user = users.find(login);
        context.setCurrentUser(user);
        return nextFilter.get().withHeader(CACHE_CONTROL, "must-revalidate");
      }
    }

    if (redirectToLogin(uri)) {
      return seeOther("/auth/login").withCookie(authCookie(buildCookie(null, uri)));
    }
    return unauthorized("private");
  }

  protected boolean redirectToLogin(String uri) {
    return true;
  }

  protected Payload signin(Context context) {
    String login = context.get("login");
    String password = context.get("password");

    User user = users.find(login, password);
    if (user == null) {
      // Unknown user. Go back to login
      return seeOther("/auth/login");
    }

    return seeOther(validRedirectUrl(readRedirectUrlInCookie(context)))
      .withCookie(authCookie(buildCookie(user, "/")));
  }

  protected Payload signout(Context context) {
    String sessionId = readSessionIdInCookie(context);
    if (sessionId != null) {
      sessionIdStore.remove(sessionId);
    }

    return seeOther("/?signout")
      .withCookie(authCookie(null));
  }

  protected String readSessionIdInCookie(Context context) {
    AuthData authData = readAuthCookie(context);
    return (authData == null) ? null : authData.sessionId;
  }

  protected String readRedirectUrlInCookie(Context context) {
    AuthData authData = readAuthCookie(context);
    String redirectUrl = (authData == null) ? null : authData.redirectAfterLogin;
    redirectUrl = (redirectUrl == null) ? "/" : redirectUrl;
    return redirectUrl;
  }

  protected String newSessionId(String login) {
    String sessionId = toHexString(RANDOM.nextLong()) + toHexString(RANDOM.nextLong());
    sessionIdStore.put(sessionId, login);
    return sessionId;
  }

  protected String buildCookie(User user, String redirectUrl) {
    AuthData cookie = new AuthData();
    if (user != null) {
      cookie.login = user.login();
      cookie.roles = user.roles();
      cookie.sessionId = newSessionId(user.login());
    }
    cookie.redirectAfterLogin = redirectUrl;

    return TypeConvert.toJson(cookie);
  }

  protected AuthData readAuthCookie(Context context) {
    try {
      return context.cookies().value(cookieName(), AuthData.class);
    } catch (Exception e) {
      // Ignore invalid cookie
      return null;
    }
  }

  protected String cookieName() {
    return "auth";
  }

  protected int expiry() {
    return ONE_DAY;
  }

  protected String domain() {
    return null;
  }

  protected Cookie authCookie(String authData) {
    NewCookie cookie = new NewCookie(cookieName(), authData, "/", true);
    cookie.setExpiry(expiry());
    cookie.setDomain(null);
    cookie.setSecure(false);
    return cookie;
  }

  protected String validRedirectUrl(String redirectUrl) {
    return redirectUrl.contains("favicon.ico") ? "/" : redirectUrl;
  }
}
