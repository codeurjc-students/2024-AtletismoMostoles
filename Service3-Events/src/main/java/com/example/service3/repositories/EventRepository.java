package com.example.service3.repositories;

import com.example.service3.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Returns all events with a date after the given one.
     * Useful for showing only upcoming or new events.
     */
    List<Event> findByDateAfter(LocalDateTime lastLoginDate);

    /**
     * Returns all events ordered by date ascending.
     */
    List<Event> findAllByOrderByDateAsc();

    List<Event> findByCreationTimeAfter(LocalDateTime lastLoginDate);

}
