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
package net.codestory.http.filters.twitter;

import java.net.*;
import java.util.*;

import twitter4j.*;
import twitter4j.auth.*;

public class TwitterAuthenticator implements Authenticator {
  private final TwitterFactory twitterFactory;
  private final Map<String, RequestToken> oauthRequestByToken;

  public TwitterAuthenticator(TwitterFactory twitterFactory) {
    this.twitterFactory = twitterFactory;
    this.oauthRequestByToken = new HashMap<>();
  }

  @Override
  public URI getAuthenticateURI(String callbackUri) {
    Twitter twitter = twitterFactory.getInstance();
    try {
      RequestToken requestToken = twitter.getOAuthRequestToken(callbackUri);
      oauthRequestByToken.put(requestToken.getToken(), requestToken);
      return URI.create(requestToken.getAuthenticationURL());
    } catch (TwitterException e) {
      throw new AuthenticationException(e);
    }
  }

  @Override
  public User authenticate(String oauthToken, String oauthVerifier) {
    Twitter twitter = twitterFactory.getInstance();
    try {
      AccessToken accessToken = twitter.getOAuthAccessToken(oauthRequestByToken.remove(oauthToken), oauthVerifier);

      twitter4j.User user = twitter.users().showUser(accessToken.getUserId());

      return new User(accessToken.getUserId(), accessToken.getScreenName(), accessToken.getToken(), accessToken.getTokenSecret(), user.getBiggerProfileImageURL());
    } catch (TwitterException e) {
      throw new AuthenticationException(e);
    }
  }
}
