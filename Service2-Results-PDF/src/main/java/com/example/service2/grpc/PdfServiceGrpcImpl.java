package com.example.service2.grpc;

import com.example.service2.services.PdfService;
import com.example.shared.CommonProto.StatusMessage;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

@GrpcService
public class PdfServiceGrpcImpl extends PdfServiceGrpc.PdfServiceImplBase {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PdfService pdfHistoryService;

    @Override
    public void requestPdfGeneration(AthleteIdRequest request, StreamObserver<StatusMessage> responseObserver) {
        // Enviar solicitud a la cola A
        rabbitTemplate.convertAndSend("cola.A", request.getAthleteId());
        StatusMessage response = StatusMessage.newBuilder()
                .setSuccess(true)
                .setMensaje("Solicitud de generación enviada")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPdfHistory(AthleteIdRequest request, StreamObserver<PdfListResponse> responseObserver) {
        List<String> urls = pdfHistoryService.getUrlsByAthleteId(request.getAthleteId());
        PdfListResponse response = PdfListResponse.newBuilder()
                .addAllUrls(urls)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
