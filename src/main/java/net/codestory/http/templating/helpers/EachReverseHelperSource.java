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
package net.codestory.http.templating.helpers;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.github.jknack.handlebars.*;

public class EachReverseHelperSource {
  public CharSequence each_reverse(Object context, Options options) throws IOException {
    if (context == null) {
      return StringUtils.EMPTY;
    }

    return (context instanceof Iterable<?>)
      ? iterableContext((Iterable<?>) context, options)
      : hashContext(context, options);
  }

  private CharSequence hashContext(Object context, Options options) throws IOException {
    StringBuilder buffer = new StringBuilder();

    Context parent = options.context;
    Iterator<Map.Entry<String, Object>> iterator = reverse(options.propertySet(context));
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      Context current = Context.newContext(parent, entry.getValue())
        .data("key", entry.getKey());
      buffer.append(options.fn(current));
    }

    return buffer;
  }

  private CharSequence iterableContext(Iterable<?> context, Options options) throws IOException {
    if (options.isFalsy(context)) {
      return options.inverse();
    }

    StringBuilder buffer = new StringBuilder();

    Iterator<?> iterator = reverse(context);
    int index = 0;
    Context parent = options.context;
    while (iterator.hasNext()) {
      Object element = iterator.next();

      boolean first = index == 0;
      boolean even = index % 2 == 0;
      boolean last = !iterator.hasNext();

      Context current = Context.newContext(parent, element)
        .data("index", index)
        .data("first", first ? "first" : "")
        .data("last", last ? "last" : "")
        .data("odd", even ? "" : "odd")
        .data("even", even ? "even" : "");
      buffer.append(options.fn(current));

      index++;
    }

    return buffer;
  }

  private static <T> Iterator<T> reverse(Iterable<T> values) {
    LinkedList<T> reversed = new LinkedList<>();
    for (T value : values) {
      reversed.add(value);
    }
    return reversed.descendingIterator();
  }
}
