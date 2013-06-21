package net.codestory.http.routes;

public interface OneParamRoute extends AnyRoute {
  Object body(String param);

  @Override
  default Object body(String[] params) {
    return body(params[0]);
  }
}
