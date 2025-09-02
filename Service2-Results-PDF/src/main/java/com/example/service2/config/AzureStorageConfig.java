package com.example.service2.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageConfig {

    String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");

    @Bean
    public BlobServiceClient blobServiceClient() {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("AZURE_STORAGE_CONNECTION_STRING is not set or empty");
        }

        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
