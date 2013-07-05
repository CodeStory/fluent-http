package net.codestory.http.filters;

import java.io.*;

import com.sun.net.httpserver.*;

public interface Filter {
  boolean apply(String uri, HttpExchange e) throws IOException;
}

