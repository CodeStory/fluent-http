package net.codestory.http.routes;

import java.io.*;

import com.sun.net.httpserver.*;

interface RouteHolder {
  boolean apply(String uri, HttpExchange exchange) throws IOException;
}
