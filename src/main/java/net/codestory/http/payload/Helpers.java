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
package net.codestory.http.payload;

import net.codestory.http.templating.*;

public interface Helpers {
  public static Payload ok() {
    return Payload.ok();
  }

  public static Payload movedPermanently(String url) {
    return Payload.movedPermanently(url);
  }

  public static Payload seeOther(String url) {
    return Payload.seeOther(url);
  }

  public static Payload notModified() {
    return Payload.notModified();
  }

  public static Payload unauthorized(String realm) {
    return Payload.unauthorized(realm);
  }

  public static Payload forbidden() {
    return Payload.forbidden();
  }

  public static Payload notFound() {
    return Payload.notFound();
  }

  public static Payload methodNotAllowed() {
    return Payload.methodNotAllowed();
  }

  public default Site site() {
    return Site.get();
  }
}
