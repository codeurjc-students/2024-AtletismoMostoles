package com.example.service3.grpc;

import com.example.service3.entities.Event;
import com.example.service3.services.EventService;
import com.example.service3.grpc.EventServiceGrpcProto.*;
import com.example.service3.grpc.EventServiceGrpc.EventServiceImplBase;
import com.example.shared.CommonProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@GrpcService
public class EventServiceGrpcImpl extends EventServiceImplBase {

    private final EventService eventService;

    public EventServiceGrpcImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void listEvents(ListEventsRequest request, StreamObserver<ListEventsResponse> responseObserver) {
        List<Event> events = eventService.findAllOrdered();

        ListEventsResponse.Builder response = ListEventsResponse.newBuilder();
        for (Event e : events) {
            response.addEvents(convertToMessage(e));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getEventById(GetEventRequest request, StreamObserver<EventMessage> responseObserver) {
        Optional<Event> optional = eventService.findById(request.getId());

        if (optional.isPresent()) {
            EventMessage msg = convertToMessage(optional.get());
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
    public void createEvent(CreateEventRequest request, StreamObserver<EventMessage> responseObserver) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDate(LocalDate.parse(request.getDate()));
        event.setMapLink(request.getMapLink());
        event.setImageLink(request.getImageLink());
        event.setOrganizedByClub(request.getOrganizedByClub());
        event.setDisciplineIds(new java.util.HashSet<>(request.getDisciplineIdsList()));
        event.setCreationTime(LocalDateTime.parse(request.getCreationTime()));

        Event saved = eventService.save(event);

        EventMessage message = convertToMessage(saved);
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void updateEvent(UpdateEventRequest request, StreamObserver<EventMessage> responseObserver) {
        Optional<Event> optional = eventService.findById(request.getId());

        if (optional.isPresent()) {
            Event event = optional.get();
            event.setName(request.getName());
            event.setDate(LocalDate.parse(request.getDate()));
            event.setMapLink(request.getMapLink());
            event.setImageLink(request.getImageLink());
            event.setOrganizedByClub(request.getOrganizedByClub());
            event.setDisciplineIds(new java.util.HashSet<>(request.getDisciplineIdsList()));
            event.setCreationTime(LocalDateTime.parse(request.getCreationTime()));

            Event updated = eventService.save(event);
            EventMessage message = convertToMessage(updated);
            responseObserver.onNext(message);
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Evento no encontrado")
                    .asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteEvent(DeleteEventRequest request, StreamObserver<CommonProto.StatusMessage> responseObserver) {
        try {
            eventService.deleteById(request.getEventId());
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
    public void pendingNotifications(NotificationsRequest request, StreamObserver<NotificationsResponse> responseObserver) {
        try {
            String fechaStr = request.getTimestampLastConnection();
            System.out.println("fecha: " + fechaStr);
            LocalDateTime date = LocalDateTime.parse(fechaStr);

            List<Event> nuevosEventos = eventService.findEventsAfter(date);

            List<NotificationData> notificaciones = nuevosEventos.stream().map(event ->
                    NotificationData.newBuilder()
                            .setEventId(event.getId())
                            .setName(event.getName())
                            .setDate(event.getDate().toString())
                            .setMapLink(event.getMapLink())
                            .setImageLink(event.getImageLink())
                            .setOrganizedByClub(event.isOrganizedByClub())
                            .setTimestampNotification(LocalDateTime.now().toString())
                            .addAllDisciplineIds(event.getDisciplineIds())
                            .build()
            ).toList();

            NotificationsResponse response = NotificationsResponse.newBuilder()
                    .addAllNotificaciones(notificaciones)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL.withDescription("Error interno").asRuntimeException());
        }
    }

    private EventMessage convertToMessage(Event event) {
        EventMessage.Builder builder = EventMessage.newBuilder()
                .setId(event.getId())
                .setName(event.getName())
                .setDate(event.getDate().toString())
                .setMapLink(event.getMapLink() != null ? event.getMapLink() : "")
                .setImageLink(event.getImageLink() != null ? event.getImageLink() : "")
                .setOrganizedByClub(event.isOrganizedByClub());

        if (event.getDisciplineIds() != null) {
            builder.addAllDisciplineIds(event.getDisciplineIds());
        }

        if (event.getCreationTime() != null) {
            builder.setCreationTime(
                    event.getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        }

        return builder.build();
    }
}
