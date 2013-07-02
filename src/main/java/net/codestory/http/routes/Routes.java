package net.codestory.http.routes;

public interface Routes {
  void serve(String fromUrl);

  void get(String uriPattern, Route route);

  void get(String uriPattern, OneParamRoute route);

  void get(String uriPattern, TwoParamsRoute route);

  void get(String uriPattern, ThreeParamsRoute route);
}
