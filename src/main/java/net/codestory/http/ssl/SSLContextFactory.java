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

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.*;

import net.codestory.http.misc.*;

import javax.net.ssl.*;

public class SSLContextFactory {
  public SSLContext create(List<Path> pathCertificates, Path pathPrivateKey, List<Path> pathTrustAnchors) throws Exception {
    X509Certificate[] chain = pathCertificates.stream().map((path) -> {
      try {
        return generateCertificateFromDER(path);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).toArray(X509Certificate[]::new);

    RSAPrivateKey key = generatePrivateKeyFromDER(pathPrivateKey);

    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(null);
    keystore.setCertificateEntry("cert-alias", chain[0]);
    keystore.setKeyEntry("key-alias", key, new char[0], chain);

    KeyStore truststore = null;
    if (pathTrustAnchors != null && !pathTrustAnchors.isEmpty()) {
      X509Certificate[] trustAnchors = pathTrustAnchors.stream().map((path) -> {
        try {
          return generateCertificateFromDER(Files.readAllBytes(path));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).toArray(X509Certificate[]::new);

      truststore = KeyStore.getInstance("JKS");
      truststore.load(null);
      for (X509Certificate certificate : trustAnchors) {
        truststore.setCertificateEntry(Md5.of(certificate.getEncoded()), certificate);
      }
    }

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(getKeyManagers(keystore), getTrustManagers(truststore), null);

    return context;
  }

  private static KeyManager[] getKeyManagers(KeyStore keyStore) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(keyStore, new char[0]);
    return kmf.getKeyManagers();
  }

  private static X509Certificate generateCertificateFromDER(Path path) throws CertificateException, IOException {
    return generateCertificateFromDER(Files.readAllBytes(path));
  }

  private static TrustManager[] getTrustManagers(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
    if (trustStore == null) {
      return null;
    }
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
    tmf.init(trustStore);
    return tmf.getTrustManagers();
  }

  private static X509Certificate generateCertificateFromDER(byte[] data) throws CertificateException {
    return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(data));
  }

  private static RSAPrivateKey generatePrivateKeyFromDER(Path path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    return generatePrivateKeyFromDER(Files.readAllBytes(path));
  }

  private static RSAPrivateKey generatePrivateKeyFromDER(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
    return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(data));
  }
}
