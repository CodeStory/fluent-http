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
package net.codestory.http.filters.basic;

import net.codestory.http.filters.Filter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class BasicAuthFilter implements Filter {
    private final String uriPrefix;
    private final String realm;
    private final Map<String, String> users;
    private final List<String> hashes;

    public BasicAuthFilter(String uriPrefix, String realm, Map<String, String> users) {
        this.realm = realm;
        this.users = users;
        this.uriPrefix = uriPrefix;
        this.hashes = new ArrayList<>();
        for (String user : users.keySet()) {
            byte[] hashByte = new String(user + ":" + users.get(user)).getBytes();
            String hash = new String(Base64.getEncoder().encode(hashByte));
            hashes.add("Basic " + hash);
        }
    }

    @Override
    public boolean apply(String uri, Request request, Response response) throws IOException {
        if (!uri.startsWith(uriPrefix)) {
            return false; // Ignore
        }
        String authorizationHeader = request.getValue("Authorization");
        if (authorizationHeader != null) {
            for (String hash : hashes) {
                if (hash.equals(authorizationHeader.trim())) {
                    return false;
                }
            }
        }
        response.setStatus(Status.UNAUTHORIZED);
        response.setContentType("text/html");
        response.getPrintStream().print("Unauthorized");
        response.setValue("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        return true;
    }
}
