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
package net.codestory.http.annotations;

import java.util.*;
import java.util.function.*;

import net.codestory.http.*;
import net.codestory.http.payload.*;

public class MethodAnnotations {
  private final List<BiFunction<Context, Supplier<Payload>, Payload>> aroundOperations;
  private final List<BiFunction<Context, Payload, Payload>> afterOperations;

  MethodAnnotations() {
    this.aroundOperations = new ArrayList<>();
    this.afterOperations = new ArrayList<>();
  }

  void addAroundOperation(BiFunction<Context, Supplier<Payload>, Payload> operation) {
    aroundOperations.add(operation);
  }

  void addAfterOperation(BiFunction<Context, Payload, Payload> operation) {
    afterOperations.add(operation);
  }

  public Payload around(Context context, Supplier<Payload> payloadSupplier) {
    Supplier<Payload> current = payloadSupplier;

    for (BiFunction<Context, Supplier<Payload>, Payload> operation : aroundOperations) {
      Supplier<Payload> last = payloadSupplier;
      current = () -> operation.apply(context, last);
    }

    return current.get();
  }

  public Payload after(Context context, Payload payload) {
    for (BiFunction<Context, Payload, Payload> operation : afterOperations) {
      payload = operation.apply(context, payload);
    }

    return payload;
  }
}
