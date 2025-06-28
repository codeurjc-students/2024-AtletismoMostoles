package com.example.service3.grpc;

import com.example.service3.entities.Event;
import com.example.service3.services.EventService;
import com.example.service3.grpc.EventoServiceGrpcProto.*;
import com.example.service3.grpc.EventoServiceGrpc.EventoServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@GrpcService
public class EventServiceGrpcImpl extends EventoServiceImplBase {

    private final EventService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public EventServiceGrpcImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void listarEventos(ListarEventosRequest request, StreamObserver<ListarEventosResponse> responseObserver) {
        List<Event> eventos = eventService.findAllOrdered();

        ListarEventosResponse.Builder response = ListarEventosResponse.newBuilder();
        for (Event e : eventos) {
            response.addEventos(convertToMessage(e));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void crearEvento(CrearEventoRequest request, StreamObserver<EventoMessage> responseObserver) {
        Event event = new Event();
        event.setName(request.getTitulo());
        event.setDate(LocalDateTime.parse(request.getFechaInicio(), formatter).toLocalDate());
        event.setMapLink(request.getUbicacion());
        event.setImageLink(request.getImagenUrl());
        event.setOrganizedByClub(true); // Puedes adaptarlo según tu lógica

        Event saved = eventService.save(event);

        EventoMessage message = convertToMessage(saved);
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void borrarEvento(BorrarEventoRequest request, StreamObserver<StatusMessage> responseObserver) {
        try {
            eventService.deleteById(request.getEventoId());
            responseObserver.onNext(StatusMessage.newBuilder()
                    .setSuccess(true)
                    .setMensaje("Evento eliminado correctamente.")
                    .build());
        } catch (Exception e) {
            responseObserver.onNext(StatusMessage.newBuilder()
                    .setSuccess(false)
                    .setMensaje("Error al eliminar el evento.")
                    .build());
        }
        responseObserver.onCompleted();
    }

    // Métodos de notificación se implementarán más adelante

    private EventoMessage convertToMessage(Event event) {
        return EventoMessage.newBuilder()
                .setId(event.getId())
                .setTitulo(event.getName())
                .setDescripcion("") // si lo agregas como campo opcional
                .setFechaInicio(event.getDate().atStartOfDay().format(formatter))
                .setFechaFin(event.getDate().plusDays(1).atStartOfDay().format(formatter)) // solo ejemplo
                .setUbicacion(event.getMapLink() != null ? event.getMapLink() : "")
                .setCreadoPor(0) // por ahora hardcoded
                .setTimestampCreacion(LocalDateTime.now().format(formatter))
                .setImagenUrl(event.getImageLink() != null ? event.getImageLink() : "")
                .build();
    }
}
