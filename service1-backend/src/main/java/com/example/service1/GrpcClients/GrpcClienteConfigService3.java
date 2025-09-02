package com.example.service1.GrpcClients;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.example.service3.grpc.EventServiceGrpc;

@Configuration
public class GrpcClienteConfigService3 {

    @Bean
    public ManagedChannel channelService3() {
        return ManagedChannelBuilder
                .forAddress("service3-backend", 9093)
                .usePlaintext()
                .build();
    }

    @Bean
    public EventServiceGrpc.EventServiceBlockingStub eventStub(ManagedChannel channelService3) {
        return EventServiceGrpc.newBlockingStub(channelService3);
    }
}
