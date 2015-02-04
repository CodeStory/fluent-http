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
package net.codestory.http.templating.helpers;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.github.jknack.handlebars.Handlebars;

public class HelperTools {
  private HelperTools() {
    // Static class
  }

  public static CharSequence toString(Object context, Function<Object, CharSequence> transform) {
    return new Handlebars.SafeString(contextAsList(context).stream().map(transform).collect(joining("\n")));
  }

  public static List<Object> contextAsList(Object context) {
    if (context instanceof Iterable<?>) {
      List<Object> list = new ArrayList<>();
      ((Iterable<?>) context).forEach(value -> list.add(value));
      return list;
    }

    return Collections.singletonList(context);
  }
}
