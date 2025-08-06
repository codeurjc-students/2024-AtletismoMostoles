package com.example.service3.GrpcServices_Test;

import com.example.service3.entities.Event;
import com.example.service3.grpc.EventServiceGrpcImpl;
import com.example.service3.services.EventService;
import com.example.service3.grpc.EventoServiceGrpcProto.*;
import com.example.shared.CommonProto;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceGrpcTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventServiceGrpcImpl grpcService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Event buildMockEvent() {
        Event e = new Event(
                "Test Event",
                LocalDate.of(2025, 7, 15),
                "https://map.link",
                "https://image.link",
                true,
                new HashSet<>(List.of(1L, 2L)),
                LocalDateTime.of(2025, 7, 14, 10, 30)
        );
        e.setId(1L);
        return e;
    }

    @Test
    void listEvents_shouldReturnList() {
        when(eventService.findAllOrdered()).thenReturn(List.of(buildMockEvent()));

        StreamObserver<ListEventsResponse> observer = new StreamObserver<>() {
            @Override
            public void onNext(ListEventsResponse value) {
                assertEquals(1, value.getEventosCount());
                assertEquals("Test Event", value.getEventos(0).getName());
            }

            @Override public void onError(Throwable t) { fail(t); }
            @Override public void onCompleted() {}
        };

        grpcService.listEvents(ListEventsRequest.newBuilder().build(), observer);
    }

    @Test
    void getEventForId_shouldReturnEvent() {
        Event event = buildMockEvent();
        when(eventService.findById(1L)).thenReturn(Optional.of(event));

        StreamObserver<EventMessage> observer = new StreamObserver<>() {
            @Override
            public void onNext(EventMessage msg) {
                assertEquals("Test Event", msg.getName());
                assertEquals(1L, msg.getId());
            }

            @Override public void onError(Throwable t) { fail(t); }
            @Override public void onCompleted() {}
        };

        grpcService.getEventForId(GetEventRequest.newBuilder().setId(1L).build(), observer);
    }

    @Test
    void getEventForId_shouldReturnNotFound() {
        when(eventService.findById(99L)).thenReturn(Optional.empty());

        StreamObserver<EventMessage> observer = new StreamObserver<>() {
            @Override public void onNext(EventMessage value) { fail("Debe lanzar error"); }
            @Override public void onError(Throwable t) {
                assertInstanceOf(StatusRuntimeException.class, t);
                assertTrue(t.getMessage().contains("no encontrado"));
            }
            @Override public void onCompleted() {}
        };

        grpcService.getEventForId(GetEventRequest.newBuilder().setId(99L).build(), observer);
    }

    @Test
    void createEvent_shouldSaveAndReturn() {
        Event saved = buildMockEvent();
        when(eventService.save(any(Event.class))).thenReturn(saved);

        CreateEventRequest request = CreateEventRequest.newBuilder()
                .setName("Test Event")
                .setDate("2025-07-15")
                .setMapLink("https://map.link")
                .setImageLink("https://image.link")
                .setOrganizedByClub(true)
                .addAllDisciplineIds(List.of(1L, 2L))
                .setCreationTime("2025-07-14T10:30:00")
                .build();

        StreamObserver<EventMessage> observer = new StreamObserver<>() {
            @Override public void onNext(EventMessage msg) {
                assertEquals("Test Event", msg.getName());
                assertEquals(1L, msg.getId());
            }

            @Override public void onError(Throwable t) { fail(t); }
            @Override public void onCompleted() {}
        };

        grpcService.createEvent(request, observer);
    }

    @Test
    void deleteEvent_shouldReturnSuccess() {
        doNothing().when(eventService).deleteById(1L);

        StreamObserver<CommonProto.StatusMessage> observer = new StreamObserver<>() {
            @Override public void onNext(CommonProto.StatusMessage msg) {
                assertTrue(msg.getSuccess());
                assertEquals("Evento eliminado correctamente.", msg.getMensaje());
            }
            @Override public void onError(Throwable t) { fail(t); }
            @Override public void onCompleted() {}
        };

        grpcService.deleteEvent(DeleteEventRequest.newBuilder().setEventId(1L).build(), observer);
    }
}
