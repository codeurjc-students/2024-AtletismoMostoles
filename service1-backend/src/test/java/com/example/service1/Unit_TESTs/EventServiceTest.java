package com.example.service1.Unit_TESTs;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.GrpcClients.EventoGrpcClient;
import com.example.service1.Services.EventoService;
import com.example.service3.grpc.EventoServiceGrpcProto.EventoMessage;
import com.example.service3.grpc.EventoServiceGrpcProto.ListarEventosResponse;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionData;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionesResponse;
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
    private EventoGrpcClient grpcClient;

    @InjectMocks
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private EventoMessage mockEventoMessage() {
        return EventoMessage.newBuilder()
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
    void testListarEventos() {
        ListarEventosResponse response = ListarEventosResponse.newBuilder()
                .addAllEventos(List.of(mockEventoMessage()))
                .build();

        when(grpcClient.listarEventos()).thenReturn(response);

        List<EventDto> result = eventoService.listarEventos();

        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getName());
    }

    @Test
    void testGetEventoById() {
        when(grpcClient.obtenerEventoPorId(1L)).thenReturn(mockEventoMessage());

        EventDto result = eventoService.getEventoById(1L);

        assertNotNull(result);
        assertEquals("Test Event", result.getName());
    }

    @Test
    void testCrearEvento() {
        EventDto input = new EventDto();
        input.setName("Nuevo");
        input.setDate(LocalDate.parse("2025-01-01"));
        input.setMapLink("map");
        input.setImageLink("img");
        input.setOrganizedByClub(true);
        input.setDisciplineIds(new java.util.HashSet<>(Arrays.asList(1L, 2L)));
        input.setCreationTime(LocalDateTime.parse("2025-01-01T12:00:00"));

        when(grpcClient.crearEvento(input)).thenReturn(mockEventoMessage());

        EventDto result = eventoService.crearEvento(input);

        assertEquals("Test Event", result.getName());
    }

    @Test
    void testActualizarEvento() {
        EventDto input = new EventDto();
        input.setName("Actualizar");
        input.setDate(LocalDate.parse("2025-01-01"));
        input.setMapLink("map");
        input.setImageLink("img");
        input.setOrganizedByClub(true);
        input.setDisciplineIds(new java.util.HashSet<>(Arrays.asList(1L)));
        input.setCreationTime(LocalDateTime.parse("2025-01-01T12:00:00"));

        when(grpcClient.actualizarEvento(1L, input)).thenReturn(mockEventoMessage());

        EventDto result = eventoService.actualizarEvento(1L, input);

        assertEquals("Test Event", result.getName());
    }

    @Test
    void testBorrarEvento() {
        // Simula retorno exitoso (como está definido en el .proto con StatusMessage)
        StatusMessage success = StatusMessage.newBuilder()
                .setSuccess(true)
                .setMensaje("ok")
                .build();

        when(grpcClient.borrarEvento(1L)).thenReturn(success);

        eventoService.borrarEvento(1L);

        verify(grpcClient).borrarEvento(1L);
    }

    @Test
    void testObtenerNotificaciones() {
        NotificacionData notif = NotificacionData.newBuilder()
                .setEventoId(1L)
                .setName("Noti")
                .setDate("2025-01-01")
                .setMapLink("map")
                .setImageLink("img")
                .setOrganizedByClub(true)
                .setTimestampNotificacion("2025-01-01T13:00:00")
                .addAllDisciplineIds(Arrays.asList(1L, 2L))
                .build();

        NotificacionesResponse response = NotificacionesResponse.newBuilder()
                .addAllNotificaciones(List.of(notif))
                .build();

        when(grpcClient.notificacionesPendientes(100L, "2025-01-01T10:00:00")).thenReturn(response);

        List<EventNotificationDto> result = eventoService.obtenerNotificaciones(100L, "2025-01-01T10:00:00");

        assertEquals(1, result.size());
        assertEquals("Noti", result.get(0).getName());
    }
}
