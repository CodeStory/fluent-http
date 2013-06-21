package net.codestory.http.types;

public class ContentTypes {
  public String get(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      return "text/plain";
    }

    String ext = filename.substring(dotIndex);
    switch (ext) {
      case ".html":
        return "text/html";
      case ".css":
        return "text/css";
      case ".zip":
        return "application/zip";
      case ".gif":
        return "image/gif";
      case ".jpeg":
        return "image/jpeg";
      case ".jpg":
        return "image/jpeg";
      case ".png":
        return "image/png";
      default:
        return "text/plain";
    }
  }
}
