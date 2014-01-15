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
package net.codestory.http.constants;

public abstract class Headers {
  private Headers() {
    // Do not allow subclassing
  }

  public static final String ACCEPT = "Accept";
  public static final String ACCEPT_CHARSET = "Accept-Charset";
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  public static final String ALLOW = "Allow";
  public static final String AUTHORIZATION = "Authorization";
  public static final String CACHE_CONTROL = "Cache-Control";
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String CONTENT_ID = "Content-ID";
  public static final String CONTENT_LANGUAGE = "Content-Language";
  public static final String CONTENT_LENGTH = "Content-Length";
  public static final String CONTENT_LOCATION = "Content-Location";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String DATE = "Date";
  public static final String ETAG = "ETag";
  public static final String EXPIRES = "Expires";
  public static final String HOST = "Host";
  public static final String IF_MATCH = "If-Match";
  public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
  public static final String IF_NONE_MATCH = "If-None-Match";
  public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
  public static final String LAST_MODIFIED = "Last-Modified";
  public static final String LOCATION = "Location";
  public static final String LINK = "Link";
  public static final String RETRY_AFTER = "Retry-After";
  public static final String USER_AGENT = "User-Agent";
  public static final String VARY = "Vary";
  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
  public static final String COOKIE = "Cookie";
  public static final String SET_COOKIE = "Set-Cookie";
  public static final String X_FORWARDED_FOR = "X-Forwarded-For";
}
