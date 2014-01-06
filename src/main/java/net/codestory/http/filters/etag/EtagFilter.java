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
package net.codestory.http.filters.etag;

import java.io.*;

import net.codestory.http.filters.*;
import net.codestory.http.internal.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;

public class EtagFilter implements Filter {
  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    Payload payload = nextFilter.get();
    if (payload == null) {
      return null;
    }

    byte[] data = payload.getData(uri, context); // TODO: Visibility and double compute
    if (data == null) {
      return payload;
    }

    if (!payload.isSuccess()) {
      return payload;
    }

    String previousEtag = context.getHeader("If-None-Match");
    String currentEtag = Md5.of(data);
    if (currentEtag.equals(previousEtag)) {
      return Payload.notModified();
    }

    return payload.withHeader("ETag", currentEtag);
  }
}
