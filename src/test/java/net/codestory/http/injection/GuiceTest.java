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

import static org.mockito.Mockito.*;

import net.codestory.http.routes.*;

import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import net.codestory.rest.FluentRestTest;
import org.junit.*;

import com.google.inject.*;

public class GuiceTest extends AbstractProdWebServerTest implements FluentRestTest {
  @Test
  public void configuration() {
    configure(new MyAppConfiguration());

    get("/").should().contain("PRODUCTION");
  }

  @Test
  public void override_bean() {
    configure(new MyAppConfiguration(new TestModule()));

    get("/").should().contain("OVERRIDDEN");
  }

  static class MyAppConfiguration extends AbstractGuiceConfiguration {
    MyAppConfiguration(Module... modules) {
      super(modules);
    }

    @Override
    protected void configure(Routes routes, Injector injector) {
      routes.get("/", () -> injector.getInstance(Service.class).hello());
    }
  }

  static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      Service service = mock(Service.class);
      bind(Service.class).toInstance(service);
      when(service.hello()).thenReturn("OVERRIDDEN");
    }
  }

  static class Service {
    public String hello() {
      return "PRODUCTION";
    }
  }
}
