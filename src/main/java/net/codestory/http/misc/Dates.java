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

import static java.time.format.DateTimeFormatter.*;

import java.time.*;
import java.util.TimeZone;

public class Dates {
  private Dates() {
    // Static class
  }

  public static String toRfc1123(long date) {
    return RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), TimeZone.getTimeZone("GMT").toZoneId()));
  }

  public static long parseRfc1123(String date) {
    return Instant.from(RFC_1123_DATE_TIME.parse(date, ZonedDateTime::from)).toEpochMilli();
  }
}
