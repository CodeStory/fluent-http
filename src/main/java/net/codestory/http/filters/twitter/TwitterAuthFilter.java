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

import net.codestory.http.filters.*;

import org.simpleframework.http.*;
import org.simpleframework.http.Query;

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

  @Override
  public boolean apply(String uri, Request request, Response response) throws IOException {
    if (!uri.startsWith(uriPrefix)) {
      return false; // Ignore
    }

    if (uri.equals(uriPrefix + "authenticate")) {
      User user;
      try {
        Query query = request.getQuery();
        String oauth_token = query.get("oauth_token");
        String oauth_verifier = query.get("oauth_verifier");

        user = twitterAuthenticator.authenticate(oauth_token, oauth_verifier);
      } catch (Exception e) {
        response.setCode(403);
        return true;
      }

      response.setCookie(new Cookie("userId", user.getId().toString(), "/", true));
      response.setCookie(new Cookie("screenName", user.getScreenName(), "/", true));
      response.setValue("Location", uriPrefix);
      response.setCode(303);
      response.setContentLength(0);

      return true;
    }

    if (uri.equals(uriPrefix + "logout")) {
      response.setCookie(new Cookie("userId", "", "/", false));
      response.setCookie(new Cookie("screenName", "", "/", false));
      response.setValue("Location", "/");
      response.setCode(303);
      response.setContentLength(0);

      return true;
    }

    Cookie userId = request.getCookie("userId");
    if ((userId != null) && !userId.getValue().isEmpty()) {
      return false; // Authenticated
    }

    String callbackUri = siteUri + uriPrefix + "authenticate";
    URI authenticateURI = twitterAuthenticator.getAuthenticateURI(callbackUri);

    response.setValue("Location", authenticateURI.toString());
    response.setCode(303);
    response.setContentLength(0);

    return true;
  }
}
