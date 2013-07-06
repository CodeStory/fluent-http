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
package net.codestory.http.dev;

import net.codestory.http.*;

public class DevMode {
  private static final String PROD_MODE = "PROD_MODE";

  private Configuration lastConfiguration;

  public static boolean isDevMode() {
    return !Boolean.parseBoolean(System.getProperty(PROD_MODE, "false"));
  }

  public void setLastConfiguration(Configuration configuration) {
    lastConfiguration = configuration;
  }

  public Configuration getLastConfiguration() {
    return lastConfiguration;
  }
}
