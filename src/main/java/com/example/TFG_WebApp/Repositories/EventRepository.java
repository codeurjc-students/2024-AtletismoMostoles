package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
