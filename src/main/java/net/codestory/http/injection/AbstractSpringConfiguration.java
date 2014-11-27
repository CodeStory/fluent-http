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
package net.codestory.http.injection;

import net.codestory.http.Configuration;
import net.codestory.http.routes.*;

import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;

public abstract class AbstractSpringConfiguration implements Configuration {
  private final BeanFactory beanFactory;

  protected AbstractSpringConfiguration(Class<?>... annotatedClasses) {
    beanFactory = new AnnotationConfigApplicationContext(annotatedClasses);
    onCreate(beanFactory);
  }

  @Override
  public final void configure(Routes routes) {
    routes.setIocAdapter(new SpringAdapter(beanFactory));
    configure(routes, beanFactory);
  }

  protected void onCreate(BeanFactory beanFactory) {
    // Do nothing by default
  }

  protected abstract void configure(Routes routes, BeanFactory beanFactory);
}
