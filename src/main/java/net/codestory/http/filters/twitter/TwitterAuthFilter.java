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
package net.codestory.http.filters.twitter;

import java.io.*;
import java.net.*;

import net.codestory.http.exchange.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;

import twitter4j.*;
import twitter4j.conf.*;

public class TwitterAuthFilter implements Filter {
  private final String siteUri;
  private final String uriPrefix;
  private final Authenticator twitterAuthenticator;

  public TwitterAuthFilter(String siteUri, String uriPrefix, String oAuthKey, String oAuthSecret) {
    this.siteUri = siteUri;
    this.uriPrefix = validPrefix(uriPrefix);
    this.twitterAuthenticator = createAuthenticator(oAuthKey, oAuthSecret);
  }

  @Override
  public boolean matches(String uri, Context context) {
    return uri.startsWith(uriPrefix);
  }

  private static String validPrefix(String prefix) {
    return prefix.endsWith("/") ? prefix : prefix + "/";
  }

  private static Authenticator createAuthenticator(String oAuthKey, String oAuthSecret) {
    Configuration config = new ConfigurationBuilder()
        .setOAuthConsumerKey(oAuthKey)
        .setOAuthConsumerSecret(oAuthSecret)
        .build();

    TwitterFactory twitterFactory = new TwitterFactory(config);

    return new TwitterAuthenticator(twitterFactory);
  }

  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    if (uri.equals(uriPrefix + "authenticate")) {
      User user;
      try {
        String oauth_token = context.get("oauth_token");
        String oauth_verifier = context.get("oauth_verifier");

        user = twitterAuthenticator.authenticate(oauth_token, oauth_verifier);
      } catch (Exception e) {
        return Payload.forbidden();
      }

      return Payload.seeOther("/")
          .withCookie(new NewCookie("userId", user.id.toString(), "/", true))
          .withCookie(new NewCookie("screenName", user.screenName, "/", true))
          .withCookie(new NewCookie("userPhoto", user.imageUrl, "/", true));
    }

    if (uri.equals(uriPrefix + "logout")) {
      return Payload.seeOther("/")
          .withCookie(new NewCookie("userId", "", "/", false))
          .withCookie(new NewCookie("screenName", "", "/", false))
          .withCookie(new NewCookie("userPhoto", "", "/", false));
    }

    String userId = context.cookies().value("userId");
    if ((userId != null) && !userId.isEmpty()) {
      context.setCurrentUser(net.codestory.http.security.User.forLogin(userId));
      return nextFilter.get(); // Authenticated
    }

    String host = context.get("Host");
    String callbackUri = ((host == null) ? siteUri : "http://" + host) + uriPrefix + "authenticate";
    URI authenticateURI = twitterAuthenticator.getAuthenticateURI(callbackUri);

    return Payload.seeOther(authenticateURI.toString());
  }
}
