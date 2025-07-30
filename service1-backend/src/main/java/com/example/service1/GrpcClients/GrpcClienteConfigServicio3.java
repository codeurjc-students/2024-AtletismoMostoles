package com.example.service1.GrpcClients;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.example.service3.grpc.EventoServiceGrpc;

@Configuration
public class GrpcClienteConfigServicio3 {

    @Bean
    public ManagedChannel channelServicio3() {
        return ManagedChannelBuilder
                .forAddress("service3-backend", 9093)
                .usePlaintext()
                .build();
    }

    @Bean
    public EventoServiceGrpc.EventoServiceBlockingStub eventoStub(ManagedChannel channelServicio3) {
        // Crea el BlockingStub para invocar RPCs de EventoService
        return EventoServiceGrpc.newBlockingStub(channelServicio3);
    }
}
