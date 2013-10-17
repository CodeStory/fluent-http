package net.codestory.http.payload;

import net.codestory.http.templating.*;

public interface Helpers {
  public default Payload seeOther(String url) {
    return Payload.seeOther(url);
  }

  public default Payload movedPermanently(String url) {
    return Payload.movedPermanently(url);
  }

  public static Payload forbidden() {
    return Payload.forbidden();
  }

  public default Site site() {
    return Site.get();
  }
}
