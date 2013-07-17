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
package net.codestory.http.injection;

import static org.fest.assertions.Assertions.*;

import java.util.*;

import org.junit.*;
import org.junit.rules.*;

public class SingletonsTest {
  Singletons singletons = new Singletons();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void create_instance() {
    Singleton instance = singletons.get(Singleton.class);

    assertThat(instance).isNotNull();
  }

  @Test
  public void create_singleton() {
    Singleton singleton1 = singletons.get(Singleton.class);
    Singleton singleton2 = singletons.get(Singleton.class);

    assertThat(singleton1).isSameAs(singleton2);
  }

  @Test
  public void inject_dependency() {
    Instance instance = singletons.get(Instance.class);

    assertThat(instance.singleton).isSameAs(singletons.get(Singleton.class));
  }

  @Test
  public void inject_dependency_with_multiple_constructors() {
    DependencyWithMultipleConstructors instance = singletons.get(DependencyWithMultipleConstructors.class);

    assertThat(instance).isNotNull();
  }

  @Test
  public void fail_without_public_constructor() {
    thrown.expect(IllegalStateException.class);

    singletons.get(Private.class);
  }

  @Test
  public void fail_with_multiple_constructors() {
    thrown.expect(IllegalStateException.class);

    singletons.get(Multiple.class);
  }

  @Test
  public void fail_with_invalid_dependency() {
    thrown.expect(IllegalStateException.class);

    singletons.get(InvalidDependency.class);
  }

  @Test
  public void fail_with_cycle_dependency() {
    thrown.expect(IllegalStateException.class);

    singletons.get(Cycle.class);
  }

  static class Singleton {
  }

  static class Cycle {
    public Cycle(Cycle cycle) {
    }
  }

  static class Private {
    private Private() {
    }
  }

  static class Multiple {
    public Multiple(Singleton singleton) {
    }

    public Multiple(Instance instance) {
    }
  }

  static class InvalidDependency {
    public InvalidDependency(Integer value) {
    }
  }

  static class DependencyWithMultipleConstructors {
    public DependencyWithMultipleConstructors(Random random) {
    }
  }

  static class Instance {
    Singleton singleton;

    public Instance(Singleton singleton) {
      this.singleton = singleton;
    }
  }
}
