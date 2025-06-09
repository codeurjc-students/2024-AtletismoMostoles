package com.example.service1.GrpcClientes;

import com.example.service3.grpc.EventoServiceGrpc;
import com.example.service3.grpc.EventoServiceGrpc.EventoServiceBlockingStub;
import com.example.service3.grpc.EventoServiceGrpcProto.BorrarEventoRequest;
import com.example.service3.grpc.EventoServiceGrpcProto.CrearEventoRequest;
import com.example.service3.grpc.EventoServiceGrpcProto.EventoMessage;
import com.example.service3.grpc.EventoServiceGrpcProto.ListarEventosRequest;
import com.example.service3.grpc.EventoServiceGrpcProto.ListarEventosResponse;
import com.example.service3.grpc.EventoServiceGrpcProto.MarcarEventoVistoRequest;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionData;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionesRequest;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionesResponse;
import com.example.service3.grpc.EventoServiceGrpcProto.StatusMessage;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.NotificacionDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventoGrpcClient {

    private final EventoServiceBlockingStub eventoStub;

    public EventoGrpcClient(EventoServiceBlockingStub eventoStub) {
        this.eventoStub = eventoStub;
    }

    /**
     * Invoca al RPC listarEventos en Servicio3 y convierte a List<EventoDto>.
     */
    public List<EventDto> listarEventos() {
        ListarEventosRequest request = ListarEventosRequest.newBuilder().build();
        ListarEventosResponse response = eventoStub.listarEventos(request);

        return response.getEventosList().stream()
                .map(e -> new EventDto(
                        e.getId(),
                        e.getTitulo(),
                        e.getDescripcion(),
                        LocalDateTime.parse(e.getFechaInicio()),
                        LocalDateTime.parse(e.getFechaFin()),
                        e.getUbicacion(),
                        e.getCreadoPor(),
                        LocalDateTime.parse(e.getTimestampCreacion())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Invoca al RPC crearEvento en Servicio3 y devuelve un EventoDto.
     */
    public EventDto crearEvento(String titulo,
                                String descripcion,
                                LocalDateTime fechaInicio,
                                LocalDateTime fechaFin,
                                String ubicacion,
                                Long creadoPor) {
        CrearEventoRequest request = CrearEventoRequest.newBuilder()
                .setTitulo(titulo)
                .setDescripcion(descripcion)
                .setFechaInicio(fechaInicio.toString())
                .setFechaFin(fechaFin.toString())
                .setUbicacion(ubicacion)
                .setCreadoPor(creadoPor)
                .build();

        EventoMessage e = eventoStub.crearEvento(request);

        return new EventDto(
                e.getId(),
                e.getTitulo(),
                e.getDescripcion(),
                LocalDateTime.parse(e.getFechaInicio()),
                LocalDateTime.parse(e.getFechaFin()),
                e.getUbicacion(),
                e.getCreadoPor(),
                LocalDateTime.parse(e.getTimestampCreacion())
        );
    }

    /**
     * Invoca al RPC borrarEvento en Servicio3 (retorna true/false).
     */
    public boolean borrarEvento(Long eventoId) {
        StatusMessage resp = eventoStub.borrarEvento(
                BorrarEventoRequest.newBuilder()
                        .setEventoId(eventoId)
                        .build()
        );
        return resp.getSuccess();
    }

    /**
     * Invoca al RPC notificacionesPendientes en Servicio3 y convierte a List<NotificacionDto>.
     */
    public List<NotificacionDto> notificacionesPendientes(Long usuarioId, LocalDateTime timestampUltimaConexion) {
        NotificacionesRequest request = NotificacionesRequest.newBuilder()
                .setUsuarioId(usuarioId)
                .setTimestampUltimaConexion(timestampUltimaConexion.toString())
                .build();

        NotificacionesResponse response = eventoStub.notificacionesPendientes(request);

        return response.getNotificacionesList().stream()
                .map(n -> new NotificacionDto(
                        n.getEventoId(),
                        n.getTitulo(),
                        n.getDescripcion(),
                        LocalDateTime.parse(n.getFechaInicio()),
                        LocalDateTime.parse(n.getFechaFin()),
                        n.getUbicacion(),
                        LocalDateTime.parse(n.getTimestampNotificacion())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Invoca al RPC marcarEventoVisto en Servicio3 (retorna true/false).
     */
    public boolean marcarEventoVisto(Long usuarioId, Long eventoId) {
        StatusMessage resp = eventoStub.marcarEventoVisto(
                MarcarEventoVistoRequest.newBuilder()
                        .setUsuarioId(usuarioId)
                        .setEventoId(eventoId)
                        .build()
        );
        return resp.getSuccess();
    }
}
