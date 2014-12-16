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
package net.codestory.http;

import net.codestory.http.injection.*;
import net.codestory.http.routes.*;

import net.codestory.rest.FluentRestTest;
import org.junit.*;
import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;

public class SpringTest implements FluentRestTest {
  WebServer server = new WebServer().startOnRandomPort();

  @Override
  public int port() {
    return server.port();
  }

  @Test
  public void configuration() {
    server.configure(new SpringConfiguration(App.class));

    get("/").should().contain("PRODUCTION");
  }

  static class SpringConfiguration extends AbstractSpringConfiguration {
    protected SpringConfiguration(Class<?>... annotatedClasses) {
      super(annotatedClasses);
    }

    @Override
    protected void configure(Routes routes, BeanFactory beanFactory) {
      routes.get("/", () -> beanFactory.getBean(Service.class).hello());
    }
  }

  @org.springframework.context.annotation.Configuration
  static class App {
    @Bean
    public Service getService() {
      return new Service();
    }
  }

  static class Service {
    public String hello() {
      return "PRODUCTION";
    }
  }
}
