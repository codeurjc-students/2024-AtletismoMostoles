package com.example.service2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = new ClassPathResource("public-cert.pem").getInputStream();
            Certificate cert = cf.generateCertificate(certInputStream);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("alias", cert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            return new RestTemplate(new SimpleClientHttpRequestFactory());

        } catch (Exception e) {
            throw new RuntimeException("Error al configurar SSL", e);
        }
    }
}
