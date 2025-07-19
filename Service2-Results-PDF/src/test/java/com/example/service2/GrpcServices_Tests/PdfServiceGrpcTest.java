package com.example.service2.GrpcServices_Tests;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.grpc.*;
import com.example.service2.services.PdfService;
import com.example.shared.CommonProto.StatusMessage;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfServiceGrpcTest {

    private PdfServiceGrpc.PdfServiceBlockingStub blockingStub;
    private RabbitTemplate rabbitTemplate;
    private PdfService pdfService;

    @BeforeEach
    void setUp() throws Exception {
        rabbitTemplate = mock(RabbitTemplate.class);
        pdfService = mock(PdfService.class);

        String serverName = InProcessServerBuilder.generateName();

        InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new PdfServiceGrpcImpl() {{
                    this.rabbitTemplate = PdfServiceGrpcTest.this.rabbitTemplate;
                    this.pdfHistoryService = PdfServiceGrpcTest.this.pdfService;
                }})
                .build()
                .start();

        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        blockingStub = PdfServiceGrpc.newBlockingStub(channel);
    }

    @Test
    void requestPdfGeneration_sendsMessageAndReturnsSuccess() {
        String athleteId = "A123";
        AthleteIdRequest request = AthleteIdRequest.newBuilder().setAthleteId(athleteId).build();

        StatusMessage response = blockingStub.requestPdfGeneration(request);

        assertTrue(response.getSuccess());
        assertEquals("Solicitud de generación enviada", response.getMensaje());

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.PDF_REQUEST_QUEUE, athleteId);
    }

    @Test
    void getPdfHistory_returnsUrlsCorrectly() {
        String athleteId = "A456";
        List<String> urls = List.of(
                "https://storage.blob.core.windows.net/pdf1.pdf",
                "https://storage.blob.core.windows.net/pdf2.pdf"
        );
        when(pdfService.getUrlsByAthleteId(athleteId)).thenReturn(urls);

        AthleteIdRequest request = AthleteIdRequest.newBuilder().setAthleteId(athleteId).build();
        PdfListResponse response = blockingStub.getPdfHistory(request);

        assertEquals(2, response.getUrlsCount());
        assertTrue(response.getUrlsList().contains("https://storage.blob.core.windows.net/pdf1.pdf"));
        assertTrue(response.getUrlsList().contains("https://storage.blob.core.windows.net/pdf2.pdf"));
    }
}
