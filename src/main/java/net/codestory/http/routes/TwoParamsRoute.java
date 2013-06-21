package net.codestory.http.routes;

public interface TwoParamsRoute extends AnyRoute {
  Object body(String param1, String param2);

  @Override
  default Object body(String[] params) {
    return body(params[0], params[1]);
  }
}
