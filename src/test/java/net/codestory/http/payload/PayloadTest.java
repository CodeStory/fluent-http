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
package net.codestory.http.payload;

import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class PayloadTest {
  @Test
  public void status_ok() {
    Payload payload = new Payload("Hello");

    assertThat(payload.code()).isEqualTo(200);
  }

  @Test
  public void custom_status() {
    Payload payload = new Payload("text/html", "Hello", 201);

    assertThat(payload.code()).isEqualTo(201);
  }
}
