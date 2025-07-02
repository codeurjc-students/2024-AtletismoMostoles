package com.example.service1.GrpcClients;

import com.example.service2.grpc.PdfServiceGrpc;
import com.example.service2.grpc.ResultServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcClienteConfigServicio2 {

    @Bean
    public ManagedChannel channelServicio2() {
        // Apunta al gRPC server de Servicio2 en localhost:9092
        return ManagedChannelBuilder
                .forAddress("localhost", 9092)
                .usePlaintext()
                .build();
    }

    @Bean
    public PdfServiceGrpc.PdfServiceBlockingStub pdfServiceStub(ManagedChannel channelServicio2) {
        return PdfServiceGrpc.newBlockingStub(channelServicio2);
    }

    @Bean
    public ResultServiceGrpc.ResultServiceBlockingStub resultServiceStub(ManagedChannel channelServicio2) {
        return ResultServiceGrpc.newBlockingStub(channelServicio2);
    }


}
