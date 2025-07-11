package com.example.service3.grpc;

import com.example.service3.entities.Event;
import com.example.service3.services.EventService;
import com.example.service3.grpc.EventoServiceGrpcProto.*;
import com.example.service3.grpc.EventoServiceGrpc.EventoServiceImplBase;
import com.example.shared.CommonProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@GrpcService
public class EventServiceGrpcImpl extends EventoServiceImplBase {

    private final EventService eventService;

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
    public void obtenerEventoPorId(GetEventoRequest request, StreamObserver<EventoMessage> responseObserver) {
        Optional<Event> optional = eventService.findById(request.getId());

        if (optional.isPresent()) {
            EventoMessage msg = convertToMessage(optional.get());
            responseObserver.onNext(msg);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Evento con ID " + request.getId() + " no encontrado.")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void crearEvento(CrearEventoRequest request, StreamObserver<EventoMessage> responseObserver) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDate(LocalDate.parse(request.getDate()));
        event.setMapLink(request.getMapLink());
        event.setImageLink(request.getImageLink());
        event.setOrganizedByClub(request.getOrganizedByClub());
        event.setDisciplineIds(new java.util.HashSet<>(request.getDisciplineIdsList()));

        Event saved = eventService.save(event);

        EventoMessage message = convertToMessage(saved);
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarEvento(UpdateEventoRequest request, StreamObserver<EventoMessage> responseObserver) {
        Optional<Event> optional = eventService.findById(request.getId());

        if (optional.isPresent()) {
            Event event = optional.get();
            event.setName(request.getName());
            event.setDate(LocalDate.parse(request.getDate()));
            event.setMapLink(request.getMapLink());
            event.setImageLink(request.getImageLink());
            event.setOrganizedByClub(request.getOrganizedByClub());
            event.setDisciplineIds(new java.util.HashSet<>(request.getDisciplineIdsList()));

            Event updated = eventService.save(event);
            EventoMessage message = convertToMessage(updated);
            responseObserver.onNext(message);
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Evento no encontrado")
                    .asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void borrarEvento(BorrarEventoRequest request, StreamObserver<CommonProto.StatusMessage> responseObserver) {
        try {
            eventService.deleteById(request.getEventoId());
            responseObserver.onNext(CommonProto.StatusMessage.newBuilder()
                    .setSuccess(true)
                    .setMensaje("Evento eliminado correctamente.")
                    .build());
        } catch (Exception e) {
            responseObserver.onNext(CommonProto.StatusMessage.newBuilder()
                    .setSuccess(false)
                    .setMensaje("Error al eliminar el evento.")
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void notificacionesPendientes(NotificacionesRequest request, StreamObserver<NotificacionesResponse> responseObserver) {
        try {
            String fechaStr = request.getTimestampUltimaConexion();
            LocalDate date = LocalDateTime.parse(fechaStr).toLocalDate();

            List<Event> nuevosEventos = eventService.findEventsAfter(date);

            List<NotificacionData> notificaciones = nuevosEventos.stream().map(event ->
                    NotificacionData.newBuilder()
                            .setEventoId(event.getId())
                            .setName(event.getName())
                            .setDate(event.getDate().toString())
                            .setMapLink(event.getMapLink())
                            .setImageLink(event.getImageLink())
                            .setOrganizedByClub(event.isOrganizedByClub())
                            .setTimestampNotificacion(LocalDateTime.now().toString())
                            .addAllDisciplineIds(event.getDisciplineIds()) // si existe
                            .build()
            ).toList();

            NotificacionesResponse response = NotificacionesResponse.newBuilder()
                    .addAllNotificaciones(notificaciones)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL.withDescription("Error interno").asRuntimeException());
        }
    }

    private EventoMessage convertToMessage(Event event) {
        EventoMessage.Builder builder = EventoMessage.newBuilder()
                .setId(event.getId())
                .setName(event.getName())
                .setDate(event.getDate().toString())
                .setMapLink(event.getMapLink() != null ? event.getMapLink() : "")
                .setImageLink(event.getImageLink() != null ? event.getImageLink() : "")
                .setOrganizedByClub(event.isOrganizedByClub());

        if (event.getDisciplineIds() != null) {
            builder.addAllDisciplineIds(event.getDisciplineIds());
        }

        return builder.build();
    }
}
