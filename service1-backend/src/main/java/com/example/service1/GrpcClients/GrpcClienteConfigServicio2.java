package com.example.service1.GrpcClients;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// IMPORTA el stub generado por shared-protos a partir de resultado_service.proto,
// según el java_package que definiste en ese .proto:
import com.example.service2.grpc.ResultadoServiceGrpc;

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
    public ResultadoServiceGrpc.ResultadoServiceBlockingStub resultadoStub(ManagedChannel channelServicio2) {
        // Crea el BlockingStub para invocar RPCs de ResultadoService
        return ResultadoServiceGrpc.newBlockingStub(channelServicio2);
    }
}
