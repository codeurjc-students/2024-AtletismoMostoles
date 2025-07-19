package com.example.service1.Services;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.GrpcClients.EventoGrpcClient;
import com.example.service3.grpc.EventoServiceGrpcProto.EventoMessage;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionData;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoGrpcClient grpcClient;

    public List<EventDto> listarEventos() {
        return grpcClient.listarEventos()
                .getEventosList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventoById(Long id) {
        EventoMessage msg = grpcClient.obtenerEventoPorId(id);
        return mapToDto(msg);
    }

    public EventDto crearEvento(EventDto dto) {
        EventoMessage msg = grpcClient.crearEvento(dto);
        return mapToDto(msg);
    }

    public EventDto actualizarEvento(Long id, EventDto dto) {
        EventoMessage msg = grpcClient.actualizarEvento(id, dto);
        return mapToDto(msg);
    }

    public void borrarEvento(Long id) {
        grpcClient.borrarEvento(id);
    }

    public List<EventNotificationDto> obtenerNotificaciones(long usuarioId, String timestampUltimaConexion) {
        NotificacionesResponse response = grpcClient.notificacionesPendientes(usuarioId, timestampUltimaConexion);
        return response.getNotificacionesList()
                .stream()
                .map(this::mapToNotificacionDto)
                .collect(Collectors.toList());
    }

    private EventDto mapToDto(EventoMessage msg) {
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

    private EventNotificationDto mapToNotificacionDto(NotificacionData notif) {
        EventNotificationDto dto = new EventNotificationDto();
        dto.setEventoId(notif.getEventoId());
        dto.setName(notif.getName());
        dto.setDate(LocalDate.parse(notif.getDate()));
        dto.setMapLink(notif.getMapLink());
        dto.setImageLink(notif.getImageLink());
        dto.setOrganizedByClub(notif.getOrganizedByClub());
        dto.setTimestampNotificacion(notif.getTimestampNotificacion());
        dto.setDisciplineIds(new HashSet<>(notif.getDisciplineIdsList()));
        return dto;
    }
}
