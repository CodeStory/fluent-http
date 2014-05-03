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
package net.codestory.http.filters.log;

import java.io.*;

import net.codestory.http.exchange.*;
import net.codestory.http.filters.*;
import net.codestory.http.payload.*;

import org.slf4j.*;

public class LogRequestFilter implements Filter {
  private final static Logger LOG = LoggerFactory.getLogger(LogRequestFilter.class);

  @Override
  public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws IOException {
    LOG.info(uri);
    return nextFilter.get();
  }
}
