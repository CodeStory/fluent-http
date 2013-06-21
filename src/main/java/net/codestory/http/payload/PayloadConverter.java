package net.codestory.http.payload;

import java.nio.charset.*;

import com.google.gson.*;

public class PayloadConverter {
  public Payload convert(Object value) {
    if (value instanceof byte[]) {
      return payload("application/octet-stream", forByteArray((byte[]) value));
    }

    if (value instanceof String) {
      return payload("text/html", forString((String) value));
    }

    return payload("application/json", forString(new Gson().toJson(value)));
  }

  static Payload payload(String contentType, byte[] data) {
    return new Payload(contentType, data);
  }

  static byte[] forByteArray(byte[] value) {
    return value;
  }

  static byte[] forString(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }
}
