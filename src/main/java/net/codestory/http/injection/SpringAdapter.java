package net.codestory.http.injection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringAdapter implements IocAdapter {
  private final ApplicationContext context;

  public SpringAdapter(ApplicationContext context) {
    this.context = context;
  }

  public SpringAdapter(Class<?> annotatedClass) {
    this.context = new AnnotationConfigApplicationContext(annotatedClass);
  }

  @Override
  public <T> T get(Class<T> type) {
    return context.getBean(type);
  }

}
