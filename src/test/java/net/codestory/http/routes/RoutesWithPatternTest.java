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
package net.codestory.http.routes;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.*;

public class RoutesWithPatternTest {
  @Test
  public void has_all_methods() {
    List<Method> methodsWithExplicitUri = new ArrayList<>();
    for (Method method : Routes.class.getDeclaredMethods()) {
      if (method.getParameterCount() > 1) {
        if (method.getParameterTypes()[0].isAssignableFrom(String.class)) {
          if (!method.getName().equals("add")) {
            methodsWithExplicitUri.add(method);
          }
        }
      }
    }

    List<Method> methodsWithImplicitUri = new ArrayList<>();
    for (Method method : RoutesWithPattern.class.getDeclaredMethods()) {
      if (!method.getName().equals("with")) {
        methodsWithImplicitUri.add(method);
      }
    }

    assertThat(methodsWithExplicitUri).hasSameSizeAs(methodsWithImplicitUri);
  }
}
