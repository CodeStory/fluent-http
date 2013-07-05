package net.codestory.http;

public class Main {
  public static void main(String[] args) throws Exception {
    new WebServer().configure(routes -> {
      routes.get("/", () -> {
        throw new IllegalStateException("BUG");
      });
    }).start(8181);
  }
}
