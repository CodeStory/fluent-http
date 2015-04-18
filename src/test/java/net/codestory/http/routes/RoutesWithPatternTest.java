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
package net.codestory.http.routes;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.*;

public class RoutesWithPatternTest {
  @Test
  public void has_all_methods() {
    List<Method> methodsWithExplicitUri = of(Routes.class.getDeclaredMethods())
      .filter(method -> method.getParameterCount() > 1)
      .filter(method -> method.getParameterTypes()[0].isAssignableFrom(String.class))
      .filter(method -> !method.getName().equals("add"))
      .collect(toList());

    List<Method> methodsWithImplicitUri = of(RoutesWithPattern.class.getDeclaredMethods())
      .filter(method -> !method.getName().equals("url"))
      .collect(toList());

    assertThat(methodsWithExplicitUri).hasSameSizeAs(methodsWithImplicitUri);
  }
}
