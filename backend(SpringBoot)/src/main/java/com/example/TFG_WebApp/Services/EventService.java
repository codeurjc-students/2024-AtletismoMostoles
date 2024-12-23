package com.example.TFG_WebApp.Services;

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

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(existingEvent -> {
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setDate(updatedEvent.getDate());
            existingEvent.setLocationLink(updatedEvent.getLocationLink());
            existingEvent.setOrganizedByClub(updatedEvent.isOrganizedByClub());
            existingEvent.setImageLink(updatedEvent.getImageLink());
            existingEvent.setDisciplines(updatedEvent.getDisciplines());
            return eventRepository.save(existingEvent);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        eventRepository.deleteById(id);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public List<Event> getAllEventsByMonthAndYear(int month, int year) {
        return eventRepository.findByMonthAndYear(month, year);
    }

    public Page<Event> getFilteredAndPaginatedEvents(Boolean upcoming, Boolean organizedByClub, Pageable pageable) {
        return eventRepository.findAll((Specification<Event>) (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (upcoming != null && upcoming) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("date"), LocalDate.now()));
            }

            if (organizedByClub != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("organizedByClub"), organizedByClub));
            }

            return predicates;
        }, pageable);
    }
}

