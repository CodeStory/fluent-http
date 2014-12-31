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
package net.codestory.http.cors;

public enum CORSRequestType {
  SIMPLE, ACTUAL, PRE_FLIGHT, NOT_CORS, INVALID_CORS;

  public boolean isCORS() {
    return this != NOT_CORS;
  }

  public boolean isPreflight() {
    return this == PRE_FLIGHT;
  }
}
