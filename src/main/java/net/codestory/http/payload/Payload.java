package net.codestory.http.payload;

public class Payload {
  public final String contentType;
  public final byte[] data;

  public Payload(String contentType, byte[] data) {
    this.contentType = contentType;
    this.data = data;
  }
}
