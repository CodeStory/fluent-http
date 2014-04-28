/**
 * Copyright (C) 2013 all@code-story.net
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;

import net.codestory.http.*;
import net.codestory.http.io.*;

import org.junit.*;

import javax.net.ssl.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SSLTest {
  @Test
  public void start_server() throws URISyntaxException {
    Path pathCertificate = resource("certificates/server.crt");
    Path pathPrivateKey = resource("certificates/server.der");

    WebServer webServer = new WebServer();
    webServer.startSSL(8183 + new Random().nextInt(1000), pathCertificate, pathPrivateKey);
  }

  @Test
  public void start_server_with_chain()
          throws URISyntaxException, NoSuchAlgorithmException, KeyStoreException, IOException,
          CertificateException, KeyManagementException {
    Path pathEECertificate = resource("certificates/ee.crt");
    Path pathSubCACertificate = resource("certificates/sub.crt");
    Path pathRootCACertificate = resource("certificates/root.crt");
    Path pathPrivateKey = resource("certificates/ee.der");

    WebServer webServer = new WebServer();
    int port = 8183 + new Random().nextInt(1000);
    webServer.startSSL(port, Arrays.asList(pathEECertificate, pathSubCACertificate), pathPrivateKey);

    URL url = new URL("https://localhost:" + port);
    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
    conn.setSSLSocketFactory(getSocketFactory(pathRootCACertificate));
    conn.setRequestMethod("GET");
    conn.getResponseCode();
    assertThat(conn.getServerCertificates()).hasSize(2);
    assertThat(conn.getServerCertificates()[0].getEncoded()).isEqualTo(getCertificateFromPath(pathEECertificate).getEncoded());
    assertThat(conn.getServerCertificates()[1].getEncoded()).isEqualTo(getCertificateFromPath(pathSubCACertificate).getEncoded());
  }

  private static Path resource(String name) throws URISyntaxException {
    return Paths.get(Resources.getResource(name).toURI());
  }

  private static SSLSocketFactory getSocketFactory(Path caCertificate) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {
    SSLContext ctx = SSLContext.getInstance("TLS");
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(null);
    ks.setCertificateEntry("rootca", getCertificateFromPath(caCertificate));
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
    tmf.init(ks);
    ctx.init(null, tmf.getTrustManagers(), null);
    return ctx.getSocketFactory();
  }

  private static Certificate getCertificateFromPath(Path certPath) throws CertificateException, IOException {
    return CertificateFactory.getInstance("X.509").generateCertificate(
                new ByteArrayInputStream(Files.readAllBytes(certPath)));
  }


}