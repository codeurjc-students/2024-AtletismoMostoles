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
            // Cargar el certificado en formato X.509 desde el classpath
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = new ClassPathResource("public-cert.pem").getInputStream();
            Certificate cert = cf.generateCertificate(certInputStream);

            // Crear un KeyStore vacío y meter el certificado
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("alias", cert);

            // Inicializar el TrustManager con ese KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Crear el contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Usar ese contexto SSL
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Crear RestTemplate con configuración por defecto
            return new RestTemplate(new SimpleClientHttpRequestFactory());

        } catch (Exception e) {
            throw new RuntimeException("Error al configurar SSL", e);
        }
    }
}
