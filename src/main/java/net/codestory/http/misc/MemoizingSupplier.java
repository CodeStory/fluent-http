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
package net.codestory.http.misc;

import java.util.function.*;

// Code copied from Google's Guava
//
public class MemoizingSupplier<T> implements Supplier<T> {
  private final Supplier<T> delegate;
  transient volatile boolean initialized;
  // "value" does not need to be volatile; visibility piggy-backs
  // on volatile read of "initialized".
  transient T value;

  private MemoizingSupplier(Supplier<T> delegate) {
    this.delegate = delegate;
  }

  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
    return new MemoizingSupplier<>(delegate);
  }

  @Override
  public T get() {
    // A 2-field variant of Double Checked Locking.
    if (!initialized) {
      synchronized (this) {
        if (!initialized) {
          T t = delegate.get();
          value = t;
          initialized = true;
          return t;
        }
      }
    }
    return value;
  }
}
