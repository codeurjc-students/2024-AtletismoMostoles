package com.example.service3.repositories;

import com.example.service3.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByDateAsc();

    List<Event> findByCreationTimeAfter(LocalDateTime lastLoginDate);

}
