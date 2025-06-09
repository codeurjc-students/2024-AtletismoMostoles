package com.example.service1.GrpcClientes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// IMPORTA el stub generado por shared-protos a partir de evento_service.proto,
// según el java_package que definiste en ese .proto:
import com.example.service3.grpc.EventoServiceGrpc;

@Configuration
public class GrpcClienteConfigServicio3 {

    @Bean
    public ManagedChannel channelServicio3() {
        // Apunta al gRPC server de Servicio3 en localhost:9093
        return ManagedChannelBuilder
                .forAddress("localhost", 9093)
                .usePlaintext()
                .build();
    }

    @Bean
    public EventoServiceGrpc.EventoServiceBlockingStub eventoStub(ManagedChannel channelServicio3) {
        // Crea el BlockingStub para invocar RPCs de EventoService
        return EventoServiceGrpc.newBlockingStub(channelServicio3);
    }
}
