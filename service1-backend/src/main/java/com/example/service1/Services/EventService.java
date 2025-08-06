package com.example.service1.Services;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.GrpcClients.EventGrpcClient;
import com.example.service3.grpc.EventServiceGrpcProto.EventMessage;
import com.example.service3.grpc.EventServiceGrpcProto.NotificationData;
import com.example.service3.grpc.EventServiceGrpcProto.NotificationsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventGrpcClient grpcClient;

    public List<EventDto> listEvents() {
        return grpcClient.listEvents()
                .getEventosList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(Long id) {
        EventMessage msg = grpcClient.getEventForId(id);
        return mapToDto(msg);
    }

    public EventDto createEvent(EventDto dto) {
        EventMessage msg = grpcClient.createEvent(dto);
        return mapToDto(msg);
    }

    public EventDto updateEvent(Long id, EventDto dto) {
        EventMessage msg = grpcClient.updateEvent(id, dto);
        return mapToDto(msg);
    }

    public void deleteEvent(Long id) {
        grpcClient.deleteEvent(id);
    }

    public List<EventNotificationDto> obtenerNotificaciones(long usuarioId, String timestampUltimaConexion) {
        NotificationsResponse response = grpcClient.pendingNotifications(usuarioId, timestampUltimaConexion);
        return response.getNotificacionesList()
                .stream()
                .map(this::mapToNotificacionDto)
                .collect(Collectors.toList());
    }

    private EventDto mapToDto(EventMessage msg) {
        EventDto dto = new EventDto();
        dto.setId(msg.getId());
        dto.setName(msg.getName());
        dto.setDate(LocalDate.parse(msg.getDate()));
        dto.setMapLink(msg.getMapLink());
        dto.setImageLink(msg.getImageLink());
        dto.setOrganizedByClub(msg.getOrganizedByClub());
        dto.setDisciplineIds(new HashSet<>(msg.getDisciplineIdsList()));
        dto.setCreationTime(LocalDateTime.parse(msg.getCreationTime()));
        return dto;
    }

    private EventNotificationDto mapToNotificacionDto(NotificationData notif) {
        EventNotificationDto dto = new EventNotificationDto();
        dto.setEventoId(notif.getEventoId());
        dto.setName(notif.getName());
        dto.setDate(LocalDate.parse(notif.getDate()));
        dto.setMapLink(notif.getMapLink());
        dto.setImageLink(notif.getImageLink());
        dto.setOrganizedByClub(notif.getOrganizedByClub());
        dto.setTimestampNotification(notif.getTimestampNotification());
        dto.setDisciplineIds(new HashSet<>(notif.getDisciplineIdsList()));
        return dto;
    }
}
