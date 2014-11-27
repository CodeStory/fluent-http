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
package net.codestory.http.filters.roles;

import java.io.*;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;

import javax.inject.*;

@Singleton
public class RoleFilter implements Filter {
  private final List<Permission> permissions = new ArrayList<>();

  public RoleFilter(Map<String, String> rolesPerUriPrefix) {
    rolesPerUriPrefix.forEach((uriPrefix, role) -> permissions.add(new Permission(uriPrefix, role)));
  }

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    String role = findRole(uri);
    if ((role != null) && (context.currentUser() != null) && !context.currentUser().isInRole(role)) {
      return Payload.forbidden();
    }

    return nextFilter.get();
  }

  // TODO: should use a Trie
  public String findRole(String uri) {
    for (Permission permission : permissions) {
      if (uri.startsWith(permission.uriPrefix)) {
        return permission.role;
      }
    }
    return null;
  }

  private static class Permission {
    final String uriPrefix;
    final String role;

    Permission(String uriPrefix, String role) {
      this.uriPrefix = uriPrefix;
      this.role = role;
    }
  }
}
