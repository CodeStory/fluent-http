package net.codestory.http.routes;

public interface FourParamsRoute extends AnyRoute {
  Object body(String param1, String param2, String param3, String param4);

  @Override
  default Object body(String[] params) {
    return body(params[0], params[1], params[2], params[3]);
  }
}
