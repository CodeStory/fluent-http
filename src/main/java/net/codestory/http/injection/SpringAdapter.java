package net.codestory.http.injection;

import org.springframework.beans.factory.*;
import org.springframework.context.annotation.*;

public class SpringAdapter implements IocAdapter {
  private final BeanFactory beanFactory;

  public SpringAdapter(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public SpringAdapter(Class<?>... annotatedClasses) {
    this(new AnnotationConfigApplicationContext(annotatedClasses));
  }

  @Override
  public <T> T get(Class<T> type) {
    return beanFactory.getBean(type);
  }
}
