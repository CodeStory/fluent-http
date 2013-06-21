package net.codestory.http.routes;

public interface Route extends AnyRoute {
  Object body();

  @Override
  default Object body(String[] params) {
    return body();
  }
}
