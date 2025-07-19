package com.example.service3.Services_Tests;

import com.example.service3.dto.EventNotification;
import com.example.service3.entities.Event;
import com.example.service3.messaging.EventNotificationSender;
import com.example.service3.repositories.EventRepository;
import com.example.service3.services.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventNotificationSender notificationSender;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEvent_sendsNotification() {
        Event event = new Event();
        event.setId(1L);
        event.setName("Evento Test");
        event.setDate(LocalDate.of(2025, 10, 10));
        event.setMapLink("https://map.com");
        event.setImageLink("https://image.com");
        event.setOrganizedByClub(true);

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event saved = eventService.save(event);

        assertNotNull(saved);
        assertEquals("Evento Test", saved.getName());
        verify(notificationSender).sendNotification(any(EventNotification.class));
        verify(eventRepository).save(event);
    }

    @Test
    void testDeleteById() {
        Long id = 123L;
        eventService.deleteById(id);
        verify(eventRepository).deleteById(id);
    }

    @Test
    void testFindById() {
        Long id = 42L;
        Event event = new Event();
        event.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.findById(id);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testFindAllOrdered() {
        List<Event> events = List.of(new Event(), new Event());
        when(eventRepository.findAllByOrderByDateAsc()).thenReturn(events);

        List<Event> result = eventService.findAllOrdered();
        assertEquals(2, result.size());
    }

    @Test
    void testFindEventsAfter() {
        LocalDateTime date = LocalDateTime.now().minusDays(5);
        List<Event> events = List.of(new Event());
        when(eventRepository.findByCreationTimeAfter(date)).thenReturn(events);

        List<Event> result = eventService.findEventsAfter(date);
        assertEquals(1, result.size());
    }
}
