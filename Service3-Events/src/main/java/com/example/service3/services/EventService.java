package com.example.service3.services;

import com.example.service3.entities.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {

    /**
     * Returns a list of all events ordered by date.
     */
    List<Event> findAllOrdered();

    /**
     * Returns all events created after a given date.
     * Used to get "new" events since last login.
     */
    List<Event> findEventsAfter(LocalDate date);

    /**
     * Finds an event by its ID.
     */
    Optional<Event> findById(Long id);

    /**
     * Creates or updates an event.
     */
    Event save(Event event);

    /**
     * Deletes an event by ID.
     */
    void deleteById(Long id);
}
