package net.codestory.http.injection;

import org.springframework.stereotype.Component;

@Component
public class Human {
  public String name = "JL";
  public int age = 42;

  public Human() {}
}