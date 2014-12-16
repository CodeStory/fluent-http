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
package net.codestory.http;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;

public class WebServerPerfTest {
  @Test
  public void launch_lots_of_servers_without_port_conflict_nor_thread_leak() {
    for (int j = 0; j < 5; j++) {
      List<WebServer> servers = new ArrayList<>();

      rangeClosed(1, 100).parallel().forEach(i -> servers.add(new WebServer().startOnRandomPort()));

      assertThat(servers).hasSize(100);

      servers.parallelStream().forEach(server -> server.stop());
    }
  }
}
