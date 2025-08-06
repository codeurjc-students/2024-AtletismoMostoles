package com.example.service3.services;

import com.example.service3.entities.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<Event> findAllOrdered();

    List<Event> findEventsAfter(LocalDateTime date);

    Optional<Event> findById(Long id);

    Event save(Event event);

    void deleteById(Long id);
}
