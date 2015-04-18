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

import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class DatesTest {
  @Test
  public void format_and_parse_in_gmt() {
    long currentTime = 1392134880000L;

    String date = Dates.toRfc1123(currentTime);
    assertThat(date).endsWith(" GMT");

    long time = Dates.parseRfc1123(date);

    assertThat(time).isEqualTo(currentTime);
  }

  @Test
  public void parse_IE_date() {
    long time = Dates.parseRfc1123("Thu, 01 Jan 1970 01:02:44 GMT");

    assertThat(time).isEqualTo(3764000);
  }
}
