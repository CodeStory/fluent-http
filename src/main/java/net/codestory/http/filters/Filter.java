package net.codestory.http.filters;

import java.io.*;

import com.sun.net.httpserver.*;

public interface Filter {
  boolean apply(HttpExchange e) throws IOException;
}

