package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Collection<Event> getNextEvents() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return eventRepository.findByDateAfterOrderByDate(yesterday);
    }

    public Collection<Event> getEventeOrganized(Boolean organizer) {
        return eventRepository.findByOrganizersOrderByDate(organizer);
    }

    // Obtener todos los eventos por mes y año
    public List<Event> getAllEvents(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return eventRepository.findByMonthAndYear(month, year);
    }

    // Obtener evento por ID
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    // Crear un nuevo evento
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    // Actualizar un evento existente
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        event.setName(eventDetails.getName());
        event.setDate(eventDetails.getDate());
        event.setDisciplines(eventDetails.getDisciplines());
        event.setResults(eventDetails.getResults());
        event.setOrganizers(eventDetails.getOrganizers());
        event.setLink_image(eventDetails.getLink_image());
        event.setLink_map(eventDetails.getLink_map());

        return eventRepository.save(event);
    }

    // Eliminar un evento
    public Optional<Event> deleteEvent(Long id) {
        Optional<Event> event = getEventById(id);
        if (event.isPresent()) {
            eventRepository.delete(event.get());
        }
        return event    ;
    }

}
