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
package net.codestory.http.internal;

import net.codestory.http.Cookie;
import net.codestory.http.Response;
import org.simpleframework.http.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class SimpleResponse implements Response {
  private final org.simpleframework.http.Response response;

  SimpleResponse(org.simpleframework.http.Response response) {
    this.response = response;
  }

  @Override
  public void close() throws IOException {
    response.close();
  }

  @Override
  public OutputStream outputStream() throws IOException {
    return response.getOutputStream();
  }

  @Override
  public void setContentLength(long length) {
    response.setContentLength(length);
  }

  @Override
  public void setHeader(String name, String value) {
    response.setValue(name, value);
  }

  @Override
  public void setStatus(int statusCode) {
    response.setStatus(Status.getStatus(statusCode));
  }

  @Override
  public void setCookie(Cookie newCookie) {
    CookieDate cookieDate = new CookieDate();
    org.simpleframework.http.Cookie cookie = new org.simpleframework.http.Cookie(newCookie.name(), newCookie.value(), newCookie.path(), newCookie.isNew()) {
      @Override
      public String toString() {
        return getName() + "=" + getValue() + "; version=" +
          getVersion() + (getPath() == null ? "" : "; path=" + getPath()) +
          (getDomain() == null ? "" : "; domain=" + getDomain()) +
          (getExpiry() < 0 ? "" : "; expires=" + cookieDate.format(getExpiry())) +
          (getExpiry() < 0 ? "" : "; max-age=" + getExpiry()) +
          (isSecure() ? "; secure" : "") +
          (isProtected() ? "; httponly" : "");
      }
    };
    cookie.setExpiry(newCookie.expiry());
    cookie.setVersion(newCookie.version());
    cookie.setSecure(newCookie.isSecure());
    cookie.setProtected(newCookie.isHttpOnly());
    cookie.setDomain(newCookie.domain());

    response.setCookie(cookie);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    return type.isInstance(response) ? (T) response : null;
  }

  private static class CookieDate {
    private static final String FORMAT = "EEE, dd-MMM-yyyy HH:mm:ss z";
    private static final String ZONE = "GMT";

    private final DateFormat format;
    private final TimeZone zone;

    public CookieDate() {
      this.format = new SimpleDateFormat(FORMAT, Locale.US);
      this.zone = new SimpleTimeZone(0, ZONE);
    }

    public String format(int seconds) {
      Calendar calendar = Calendar.getInstance(zone, Locale.US);
      Date date = convert(seconds);

      calendar.setTime(date);
      format.setCalendar(calendar);

      return format.format(date);
    }

    private Date convert(int seconds) {
      long now = System.currentTimeMillis();
      long duration = seconds * 1000L;
      long time = now + duration;

      return new Date(time);
    }
  }
}
