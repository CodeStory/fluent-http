/**
 * Copyright (C) 2013-2015 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http.ssl;

import net.codestory.http.WebServer;
import net.codestory.http.io.ClassPaths;
import org.junit.After;
import org.junit.Test;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class SSLTest {
  WebServer webServer = new WebServer();

  @After
  public void tearDown() {
    if (webServer != null) {
      webServer.stop();
    }
  }

  @Test
  public void start_server() throws URISyntaxException {
    Path pathCertificate = resource("certificates/server.crt");
    Path pathPrivateKey = resource("certificates/server.der");

    webServer.startSSL(0, pathCertificate, pathPrivateKey);
  }

  @Test
  public void certificate_chain() throws Exception {
    Path pathEECertificate = resource("certificates/ee.crt");
    Path pathSubCACertificate = resource("certificates/sub.crt");
    Path pathRootCACertificate = resource("certificates/root.crt");
    Path pathPrivateKey = resource("certificates/ee.der");

    webServer.startSSL(0, asList(pathEECertificate, pathSubCACertificate), pathPrivateKey);

    HttpsURLConnection conn = httpGet(webServer, getSocketFactory(pathRootCACertificate));

    assertThat(conn.getServerCertificates()).hasSize(2);
    assertThat(conn.getServerCertificates()[0].getEncoded()).isEqualTo(getCertificateFromPath(pathEECertificate).getEncoded());
    assertThat(conn.getServerCertificates()[1].getEncoded()).isEqualTo(getCertificateFromPath(pathSubCACertificate).getEncoded());
  }

  @Test
  public void client_auth() throws Exception {
    Path pathEECertificate = resource("certificates/ee.crt");
    Path pathSubCACertificate = resource("certificates/sub.crt");
    Path pathPrivateKey = resource("certificates/ee.der");
    Path pathTrustAnchors = resource("certificates/root.crt");
    Path pathClientCertificate = resource("certificates/client.pfx");

    webServer.startSSL(0, asList(pathEECertificate, pathSubCACertificate), pathPrivateKey, singletonList(pathTrustAnchors));

    httpGet(webServer, getSocketFactory(pathTrustAnchors, pathClientCertificate));
  }

  @Test(expected = Exception.class)
  public void client_auth_failure() throws Exception {
    Path pathEECertificate = resource("certificates/ee.crt");
    Path pathSubCACertificate = resource("certificates/sub.crt");
    Path pathPrivateKey = resource("certificates/ee.der");
    Path pathTrustAnchors = resource("certificates/root.crt");

    webServer.startSSL(0, asList(pathEECertificate, pathSubCACertificate), pathPrivateKey, singletonList(pathTrustAnchors));

    httpGet(webServer, getSocketFactory(pathTrustAnchors));
  }

  private HttpsURLConnection httpGet(WebServer webServer, SSLSocketFactory socketFactory) throws IOException {
    URL url = new URL("https://localhost:" + webServer.port());
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    conn.setSSLSocketFactory(socketFactory);
    conn.setRequestMethod("GET");
    conn.getResponseCode();
    return conn;
  }

  private static Path resource(String name) throws URISyntaxException {
    return Paths.get(ClassPaths.getResource(name).toURI());
  }

  private static SSLSocketFactory getSocketFactory(Path caCertificate) throws Exception {
    return getSocketFactory(caCertificate, null);
  }

  private static SSLSocketFactory getSocketFactory(Path caCertificate, Path clientCertificate) throws Exception {
//    System.setProperty("https.protocols", "SSLv3");
    SSLContext ctx = SSLContext.getInstance("TLS");

    KeyStore ts = KeyStore.getInstance("JKS");
    ts.load(null);
    ts.setCertificateEntry("rootca", getCertificateFromPath(caCertificate));
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
    tmf.init(ts);
    TrustManager[] tm = tmf.getTrustManagers();

    KeyManager[] km = null;
    if (clientCertificate != null) {
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(Files.newInputStream(clientCertificate), "password".toCharArray());
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
      kmf.init(ks, "password".toCharArray());
      km = kmf.getKeyManagers();
    }

    ctx.init(km, tm, null);
    return ctx.getSocketFactory();
  }

  private static Certificate getCertificateFromPath(Path certPath) throws CertificateException, IOException {
    return CertificateFactory.getInstance("X.509").generateCertificate(
      new ByteArrayInputStream(Files.readAllBytes(certPath)));
  }
}
