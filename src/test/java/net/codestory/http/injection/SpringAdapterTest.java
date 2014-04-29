package net.codestory.http.injection;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;


public class SpringAdapterTest {

  @Test
  public void inject_bean_with_configuration_classes() {
    SpringAdapter springAdapter = new SpringAdapter(SpringConfiguration.class);
    assertThat(springAdapter.get(Human.class).name).isEqualTo("JL");
    assertThat(springAdapter.get(Human.class).age).isEqualTo(42);
  }

  @Test
  public void inject_spring_bean_with_context() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    SpringAdapter springAdapter = new SpringAdapter(applicationContext);
    assertThat(springAdapter.get(Human.class).name).isEqualTo("JL");
    assertThat(springAdapter.get(Human.class).age).isEqualTo(42);
  }

}