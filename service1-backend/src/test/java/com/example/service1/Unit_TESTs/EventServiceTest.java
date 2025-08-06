package com.example.service1.Unit_TESTs;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.GrpcClients.EventGrpcClient;
import com.example.service1.Services.EventService;
import com.example.service3.grpc.EventServiceGrpcProto.EventMessage;
import com.example.service3.grpc.EventServiceGrpcProto.ListEventsResponse;
import com.example.service3.grpc.EventServiceGrpcProto.NotificationData;
import com.example.service3.grpc.EventServiceGrpcProto.NotificationsResponse;
import com.example.shared.CommonProto.StatusMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventGrpcClient grpcClient;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private EventMessage mockEventMessage() {
        return EventMessage.newBuilder()
                .setId(1L)
                .setName("Test Event")
                .setDate("2025-01-01")
                .setMapLink("http://map")
                .setImageLink("http://img")
                .setOrganizedByClub(true)
                .addAllDisciplineIds(Arrays.asList(1L, 2L))
                .setCreationTime("2025-01-01T12:00:00")
                .build();
    }

    @Test
    void testListEvents() {
        ListEventsResponse response = ListEventsResponse.newBuilder()
                .addAllEventos(List.of(mockEventMessage()))
                .build();

        when(grpcClient.listEvents()).thenReturn(response);

        List<EventDto> result = eventService.listEvents();

        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getName());
    }

    @Test
    void testGetEventById() {
        when(grpcClient.getEventForId(1L)).thenReturn(mockEventMessage());

        EventDto result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals("Test Event", result.getName());
    }

    @Test
    void testCreateEvent() {
        EventDto input = new EventDto();
        input.setName("Nuevo");
        input.setDate(LocalDate.parse("2025-01-01"));
        input.setMapLink("map");
        input.setImageLink("img");
        input.setOrganizedByClub(true);
        input.setDisciplineIds(new java.util.HashSet<>(Arrays.asList(1L, 2L)));
        input.setCreationTime(LocalDateTime.parse("2025-01-01T12:00:00"));

        when(grpcClient.createEvent(input)).thenReturn(mockEventMessage());

        EventDto result = eventService.createEvent(input);

        assertEquals("Test Event", result.getName());
    }

    @Test
    void testUpdateEvent() {
        EventDto input = new EventDto();
        input.setName("Actualizar");
        input.setDate(LocalDate.parse("2025-01-01"));
        input.setMapLink("map");
        input.setImageLink("img");
        input.setOrganizedByClub(true);
        input.setDisciplineIds(new java.util.HashSet<>(Arrays.asList(1L)));
        input.setCreationTime(LocalDateTime.parse("2025-01-01T12:00:00"));

        when(grpcClient.updateEvent(1L, input)).thenReturn(mockEventMessage());

        EventDto result = eventService.updateEvent(1L, input);

        assertEquals("Test Event", result.getName());
    }

    @Test
    void testDeleteEvent() {
        StatusMessage success = StatusMessage.newBuilder()
                .setSuccess(true)
                .setMensaje("ok")
                .build();

        when(grpcClient.deleteEvent(1L)).thenReturn(success);

        eventService.deleteEvent(1L);

        verify(grpcClient).deleteEvent(1L);
    }

    @Test
    void testGetNotifications() {
        NotificationData notif = NotificationData.newBuilder()
                .setEventoId(1L)
                .setName("Noti")
                .setDate("2025-01-01")
                .setMapLink("map")
                .setImageLink("img")
                .setOrganizedByClub(true)
                .setTimestampNotification("2025-01-01T13:00:00")
                .addAllDisciplineIds(Arrays.asList(1L, 2L))
                .build();

        NotificationsResponse response = NotificationsResponse.newBuilder()
                .addAllNotificaciones(List.of(notif))
                .build();

        when(grpcClient.pendingNotifications(100L, "2025-01-01T10:00:00")).thenReturn(response);

        List<EventNotificationDto> result = eventService.obtenerNotificaciones(100L, "2025-01-01T10:00:00");

        assertEquals(1, result.size());
        assertEquals("Noti", result.get(0).getName());
    }
}
