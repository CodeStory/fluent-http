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
package net.codestory.http.exchange;

import java.util.*;

public interface Cookies extends Iterable<Cookie> {
  Cookie get(String name);

  String value(String name);

  Map<String, String> keyValues();

  <T> T value(String name, T defaultValue);

  <T> T value(String name, Class<T> type);

  String value(String name, String defaultValue);

  int value(String name, int defaultValue);

  long value(String name, long defaultValue);

  boolean value(String name, boolean defaultValue);
}
