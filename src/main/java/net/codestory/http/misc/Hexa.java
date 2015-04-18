/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.http.misc;

public class Hexa {
  private Hexa() {
    // Utility class
  }

  public static String toHex(byte[] hash) {
    StringBuilder result = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xFF & b);
      if (hex.length() == 1) {
        result.append("0");
      }
      result.append(hex);
    }
    return result.toString();
  }
}
