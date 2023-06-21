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

import net.codestory.http.*;
import net.codestory.http.routes.*;

import com.google.inject.*;

public class GuiceConfiguration implements Configuration {
  private final Configuration configuration;
  private final Injector injector;

  protected GuiceConfiguration(com.google.inject.Module module, Configuration configuration) {
    this.configuration = configuration;
    this.injector = Guice.createInjector(module);
    onCreateInjector(this.injector);
  }

  @Override
  public final void configure(Routes routes) {
    routes.setIocAdapter(new GuiceAdapter(injector));
    configuration.configure(routes);
  }

  protected void onCreateInjector(Injector injector) {
    // Do nothing by default
  }
}
