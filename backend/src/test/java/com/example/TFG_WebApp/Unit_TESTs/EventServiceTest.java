package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Repositories.EventRepository;
import com.example.TFG_WebApp.Services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void testGetEvents_All() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventsPage = new PageImpl<>(Collections.singletonList(new Event()));

        when(eventRepository.findAll(pageable)).thenReturn(eventsPage);

        Page<Event> result = eventService.getEvents(pageable, null, null, null);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).findAll(pageable);
    }

    @Test
    void testGetEvents_ByOrganizedByClub() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventRepository.findByIsOrganizedByClub(true, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Event())));

        Page<Event> result = eventService.getEvents(pageable, true, null, null);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).findByIsOrganizedByClub(true, pageable);
    }

    @Test
    void testGetEvents_ByDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now().plusDays(5);

        when(eventRepository.findByDateBetween(start, end, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Event())));

        Page<Event> result = eventService.getEvents(pageable, null, start, end);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).findByDateBetween(start, end, pageable);
    }

    @Test
    void testGetEvents_ByOrganizedAndDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now().plusDays(5);

        when(eventRepository.findByIsOrganizedByClubAndDateBetween(true, start, end, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Event())));

        Page<Event> result = eventService.getEvents(pageable, true, start, end);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).findByIsOrganizedByClubAndDateBetween(true, start, end, pageable);
    }

    @Test
    void testGetEventById_Success() {
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(1L);

        assertEquals(1L, result.getId());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(99L);
        });

        verify(eventRepository).findById(99L);
    }

    @Test
    void testCreateEvent() {
        Event event = new Event();
        event.setName("New Event");

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.createEvent(event);

        assertEquals("New Event", result.getName());
        verify(eventRepository).save(event);
    }

    @Test
    void testUpdateEvent() {
        Event existing = new Event();
        existing.setId(1L);
        existing.setName("Old Name");

        Event updated = new Event();
        updated.setName("Updated Name");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(existing)).thenReturn(existing);

        Event result = eventService.updateEvent(1L, updated);

        assertEquals("Updated Name", result.getName());
        verify(eventRepository).save(existing);
    }

    @Test
    void testDeleteEvent() {
        Event event = new Event();
        event.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).delete(event);

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }
}
