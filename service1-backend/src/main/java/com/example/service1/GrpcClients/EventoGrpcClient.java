package com.example.service1.GrpcClients;

import com.example.service1.DTO.EventDto;
import com.example.service3.grpc.EventoServiceGrpc;
import com.example.service3.grpc.EventoServiceGrpcProto.*;
import com.example.shared.CommonProto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import io.grpc.ManagedChannel;

@Component
public class EventoGrpcClient {

    private final EventoServiceGrpc.EventoServiceBlockingStub eventoStub;

    public EventoGrpcClient(@Qualifier("channelServicio3") ManagedChannel channel) {
        this.eventoStub = EventoServiceGrpc.newBlockingStub(channel);
    }

    public ListarEventosResponse listarEventos() {
        return eventoStub.listarEventos(ListarEventosRequest.newBuilder().build());
    }

    public EventoMessage crearEvento(EventDto dto) {
        CrearEventoRequest.Builder builder = CrearEventoRequest.newBuilder()
                .setName(dto.getName())
                .setDate(dto.getDate().toString())
                .setMapLink(dto.getMapLink() != null ? dto.getMapLink() : "")
                .setImageLink(dto.getImageLink() != null ? dto.getImageLink() : "")
                .setOrganizedByClub(dto.isOrganizedByClub())
                .setCreationTime(dto.getCreationTime().toString());

        if (dto.getDisciplineIds() != null) {
            builder.addAllDisciplineIds(dto.getDisciplineIds());
        }

        return eventoStub.crearEvento(builder.build());
    }

    public EventoMessage obtenerEventoPorId(Long id) {
        GetEventoRequest request = GetEventoRequest.newBuilder().setId(id).build();
        return eventoStub.obtenerEventoPorId(request);
    }

    public EventoMessage actualizarEvento(Long id, EventDto dto) {
        UpdateEventoRequest.Builder builder = UpdateEventoRequest.newBuilder()
                .setId(id)
                .setName(dto.getName())
                .setDate(dto.getDate().toString())
                .setMapLink(dto.getMapLink() != null ? dto.getMapLink() : "")
                .setImageLink(dto.getImageLink() != null ? dto.getImageLink() : "")
                .setOrganizedByClub(dto.isOrganizedByClub())
                .setCreationTime(dto.getCreationTime().toString());

        if (dto.getDisciplineIds() != null) {
            builder.addAllDisciplineIds(dto.getDisciplineIds());
        }

        return eventoStub.actualizarEvento(builder.build());
    }

    public CommonProto.StatusMessage borrarEvento(long eventoId) {
        BorrarEventoRequest request = BorrarEventoRequest.newBuilder().setEventoId(eventoId).build();
        return eventoStub.borrarEvento(request);
    }

    public NotificacionesResponse notificacionesPendientes(long usuarioId, String timestampUltimaConexion) {
        NotificacionesRequest request = NotificacionesRequest.newBuilder()
                .setUsuarioId(usuarioId)
                .setTimestampUltimaConexion(timestampUltimaConexion)
                .build();
        return eventoStub.notificacionesPendientes(request);
    }

    public CommonProto.StatusMessage marcarEventoVisto(long usuarioId, long eventoId) {
        MarcarEventoVistoRequest request = MarcarEventoVistoRequest.newBuilder()
                .setUsuarioId(usuarioId)
                .setEventoId(eventoId)
                .build();
        return eventoStub.marcarEventoVisto(request);
    }
}
