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
package net.codestory.http.annotations;

import net.codestory.http.Context;
import net.codestory.http.payload.Payload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MethodAnnotations {
  private final List<BiFunction<Context, Function<Context, Payload>, Payload>> operations;

  MethodAnnotations() {
    this.operations = new ArrayList<>();
  }

  void addAroundOperation(BiFunction<Context, Function<Context, Payload>, Payload> operation) {
    operations.add(operation);
  }

  void addAfterOperation(BiFunction<Context, Payload, Payload> operation) {
    operations.add((context, payloadSupplier) -> operation.apply(context, payloadSupplier.apply(context)));
  }

  public Payload apply(Context context, Function<Context, Payload> payloadSupplier) {
    Function<Context, Payload> current = payloadSupplier;

    for (BiFunction<Context, Function<Context, Payload>, Payload> operation : operations) {
      current = ctx -> operation.apply(ctx, payloadSupplier);
    }

    return current.apply(context);
  }
}
