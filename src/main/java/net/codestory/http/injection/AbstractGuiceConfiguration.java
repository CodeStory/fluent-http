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
package net.codestory.http.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.codestory.http.Configuration;
import net.codestory.http.routes.Routes;


public abstract class AbstractGuiceConfiguration implements Configuration {
  private final Injector injector;

  protected AbstractGuiceConfiguration(com.google.inject.Module... modules) {
    injector = Guice.createInjector(modules);
    onCreate(injector);
  }

  @Override
  public final void configure(Routes routes) {
    routes.setIocAdapter(new GuiceAdapter(injector));
    configure(routes, injector);
  }

  protected void onCreate(Injector injector) {
    // Do nothing by default
  }

  protected abstract void configure(Routes routes, Injector injector);
}
