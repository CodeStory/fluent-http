package net.codestory.http.servlet;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import net.codestory.http.Configuration;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import javax.ws.rs.Path;
import java.io.IOException;
import java.net.InetSocketAddress;

import static com.sun.jersey.api.container.ContainerFactory.createContainer;
import static com.sun.jersey.api.core.ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS;

public class Jersey {
  private Configuration configuration;
  private Connection server;

  public Jersey(Configuration configuration) {
    this.configuration = configuration;
  }

  public static void main(String[] args) throws IOException {
    Jersey jersey = new Jersey(routes -> routes.get("/", "Hello"));
    jersey.start(8080);
  }

  public void start(int port) throws IOException {
    ResourceConfig resourceConfig = new DefaultResourceConfig(EmptyResource.class);
    resourceConfig.getProperties().put(PROPERTY_CONTAINER_RESPONSE_FILTERS, new FluentHttpServletFilter(configuration));

    server = new SocketConnection(new ContainerSocketProcessor(createContainer(Container.class, resourceConfig)));
    server.connect(new InetSocketAddress(port), null);
  }

  public void stop() throws IOException {
    server.close();
  }

  @Path("/")
  public static class EmptyResource {
  }
}