package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Page<Event> getEvents(Pageable pageable, Boolean organizedByClub, LocalDate startDate, LocalDate endDate) {
        if (organizedByClub != null && startDate != null && endDate != null) {
            return eventRepository.findByIsOrganizedByClubAndDateBetween(organizedByClub, startDate, endDate, pageable);
        } else if (organizedByClub != null) {
            return eventRepository.findByIsOrganizedByClub(organizedByClub, pageable);
        } else if (startDate != null && endDate != null) {
            return eventRepository.findByDateBetween(startDate, endDate, pageable);
        } else {
            return eventRepository.findAll(pageable);
        }
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = getEventById(id);
        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setMapLink(updatedEvent.getMapLink());
        existingEvent.setOrganizedByClub(updatedEvent.isOrganizedByClub());
        existingEvent.setDisciplines(updatedEvent.getDisciplines());
        existingEvent.setResults(updatedEvent.getResults());
        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
}


