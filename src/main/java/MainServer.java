import net.codestory.http.*;

public class MainServer {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getProperty("app.port", "8080"));

    WebServer webServer = new WebServer();

    //webServer.get("/", () -> "Hello World");

    webServer.start(port);
  }
}
