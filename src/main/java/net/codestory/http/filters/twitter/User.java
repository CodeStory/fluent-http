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

public class User {
  final Long id;
  final String screenName;
  final String token;
  final String secret;
  final String imageUrl;

  public User(Long id, String screenName, String token, String secret, String imageUrl) {
    this.id = id;
    this.screenName = screenName;
    this.token = token;
    this.secret = secret;
    this.imageUrl = imageUrl;
  }
}
