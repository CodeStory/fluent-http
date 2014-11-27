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

import static org.assertj.core.api.Assertions.*;

import org.junit.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

public class SpringAdapterTest {
  @Test
  public void inject_bean_with_configuration_classes() {
    SpringAdapter adapter = new SpringAdapter(SpringConfiguration.class);

    Human human = adapter.get(Human.class);
    assertThat(human.name).isEqualTo("JL");
    assertThat(human.age).isEqualTo(42);
  }

  @Test
  public void inject_spring_bean_with_context() {
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

    SpringAdapter adapter = new SpringAdapter(context);

    Human human = adapter.get(Human.class);
    assertThat(human.name).isEqualTo("JL");
    assertThat(human.age).isEqualTo(42);
  }

  @Configuration
  @ComponentScan
  static class SpringConfiguration {
  }

  @Component
  static class Human {
    public String name = "JL";
    public int age = 42;

    public Human() {
    }
  }
}