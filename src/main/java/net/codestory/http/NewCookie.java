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
package net.codestory.http;

public class NewCookie implements Cookie {
  private String name;
  private String value;
  private String path;
  private String domain;
  private boolean secure;
  private boolean httpOnly;
  private boolean created;
  private int version;
  private int expiry;

  public NewCookie(String name, String value) {
    this(name, value, "/");
  }

  public NewCookie(String name, String value, boolean created) {
    this(name, value, "/", created);
  }

  public NewCookie(String name, String value, String path) {
    this(name, value, path, false);
  }

  public NewCookie(String name, String value, String path, boolean created) {
    this.name = name;
    this.value = value;
    this.path = path;
    this.created = created;
    this.version = 1;
    this.expiry = -1;
  }

  public boolean isNew() {
    return created;
  }

  @Override
  public int version() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String value() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  @Override
  public boolean isHttpOnly() {
    return httpOnly;
  }

  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  @Override
  public int expiry() {
    return expiry;
  }

  public void setExpiry(int expiry) {
    this.expiry = expiry;
  }

  @Override
  public String path() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String domain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Override
  public <T> T unwrap(Class<T> type) {
    throw new UnsupportedOperationException();
  }
}
