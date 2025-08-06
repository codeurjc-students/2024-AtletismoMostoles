package com.example.service1.GrpcClients;

import com.example.service2.grpc.PdfServiceGrpc;
import com.example.service2.grpc.ResultServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcClienteConfigService2 {

    @Bean
    public ManagedChannel channelService2() {
        return ManagedChannelBuilder
                .forAddress("service2-backend", 9092)
                .usePlaintext()
                .build();
    }

    @Bean
    public PdfServiceGrpc.PdfServiceBlockingStub pdfServiceStub(ManagedChannel channelService2) {
        return PdfServiceGrpc.newBlockingStub(channelService2);
    }

    @Bean
    public ResultServiceGrpc.ResultServiceBlockingStub resultServiceStub(ManagedChannel channelService2) {
        return ResultServiceGrpc.newBlockingStub(channelService2);
    }


}
