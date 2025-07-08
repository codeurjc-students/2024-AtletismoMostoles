package com.example.service2.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class AzureBlobService {

    private static final String CONTAINER_NAME = "resultspdf";

    @Autowired
    private BlobServiceClient blobServiceClient;

    public String uploadPdf(String fileName, byte[] content) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!containerClient.exists()) {
            containerClient.create();
        }

        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(new ByteArrayInputStream(content), content.length, true);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType("application/pdf");
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }
}
