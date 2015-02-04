/**
 * Copyright (C) 2013-2014 all@code-story.net
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import net.codestory.http.misc.Md5;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextFactory {
  public SSLContext create(List<Path> pathCertificates, Path pathPrivateKey, List<Path> pathTrustAnchors) throws GeneralSecurityException, IOException {
    X509Certificate[] chain = pathCertificates
      .stream()
      .map(path -> generateCertificateFromDER(path))
      .toArray(X509Certificate[]::new);

    RSAPrivateKey key = generatePrivateKeyFromDER(pathPrivateKey);

    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null);
    keyStore.setCertificateEntry("cert-alias", chain[0]);
    keyStore.setKeyEntry("key-alias", key, new char[0], chain);

    KeyStore trustStore;
    if (pathTrustAnchors == null || pathTrustAnchors.isEmpty()) {
      trustStore = null;
    } else {
      trustStore = KeyStore.getInstance("JKS");
      trustStore.load(null);

      for (Path path : pathTrustAnchors) {
        X509Certificate certificate = generateCertificateFromDER(path);
        trustStore.setCertificateEntry(Md5.of(certificate.getEncoded()), certificate);
      }
    }

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(getKeyManagers(keyStore), getTrustManagers(trustStore), null);

    return context;
  }

  private static KeyManager[] getKeyManagers(KeyStore keyStore) throws GeneralSecurityException {
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(keyStore, new char[0]);
    return kmf.getKeyManagers();
  }

  private static TrustManager[] getTrustManagers(KeyStore trustStore) throws GeneralSecurityException {
    if (trustStore == null) {
      return null;
    }
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
    tmf.init(trustStore);
    return tmf.getTrustManagers();
  }

  private static X509Certificate generateCertificateFromDER(Path path) {
    try {
      return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(Files.readAllBytes(path)));
    } catch (GeneralSecurityException | IOException e) {
      throw new IllegalStateException("Unable to generate certificate", e);
    }
  }

  private static RSAPrivateKey generatePrivateKeyFromDER(Path path) {
    try {
      return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(path)));
    } catch (GeneralSecurityException | IOException e) {
      throw new IllegalStateException("Unable to generate private key", e);
    }
  }
}
