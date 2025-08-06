package com.example.service1.GrpcClients;

import com.example.service1.DTO.EventDto;
import com.example.service3.grpc.EventServiceGrpc;
import com.example.service3.grpc.EventServiceGrpcProto.*;
import com.example.shared.CommonProto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import io.grpc.ManagedChannel;

@Component
public class EventGrpcClient {

    private final EventServiceGrpc.EventServiceBlockingStub eventoStub;

    public EventGrpcClient(@Qualifier("channelService3") ManagedChannel channel) {
        this.eventoStub = EventServiceGrpc.newBlockingStub(channel);
    }

    public ListEventsResponse listEvents() {
        return eventoStub.listEvents(ListEventsRequest.newBuilder().build());
    }

    public EventMessage createEvent(EventDto dto) {
        CreateEventRequest.Builder builder = CreateEventRequest.newBuilder()
                .setName(dto.getName())
                .setDate(dto.getDate().toString())
                .setMapLink(dto.getMapLink() != null ? dto.getMapLink() : "")
                .setImageLink(dto.getImageLink() != null ? dto.getImageLink() : "")
                .setOrganizedByClub(dto.isOrganizedByClub())
                .setCreationTime(dto.getCreationTime().toString());

        if (dto.getDisciplineIds() != null) {
            builder.addAllDisciplineIds(dto.getDisciplineIds());
        }

        return eventoStub.createEvent(builder.build());
    }

    public EventMessage getEventForId(Long id) {
        GetEventRequest request = GetEventRequest.newBuilder().setId(id).build();
        return eventoStub.getEventById(request);
    }

    public EventMessage updateEvent(Long id, EventDto dto) {
        UpdateEventRequest.Builder builder = UpdateEventRequest.newBuilder()
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

        return eventoStub.updateEvent(builder.build());
    }

    public CommonProto.StatusMessage deleteEvent(long eventId) {
        DeleteEventRequest request = DeleteEventRequest.newBuilder().setEventId(eventId).build();
        return eventoStub.deleteEvent(request);
    }

    public NotificationsResponse pendingNotifications(long userId, String timestampLastConnection) {
        NotificationsRequest request = NotificationsRequest.newBuilder()
                .setUserId(userId)
                .setTimestampLastConnection(timestampLastConnection)
                .build();
        return eventoStub.pendingNotifications(request);
    }

}
