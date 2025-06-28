package com.example.service3.services;

import com.example.service3.entities.Event;
import com.example.service3.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAllOrdered() {
        return eventRepository.findAllByOrderByDateAsc();
    }

    @Override
    public List<Event> findEventsAfter(LocalDate date) {
        return eventRepository.findByDateAfter(date);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }
}
