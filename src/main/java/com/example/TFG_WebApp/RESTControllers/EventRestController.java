package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/calendar")
    public List<Event> getAllEvents(@RequestParam int month, @RequestParam int year) {
        return eventService.getAllEventsByMonthAndYear(month + 1, year);
    }

    @GetMapping
    public ResponseEntity<Page<Event>> getFilteredAndPaginatedEvents(
            @RequestParam(required = false) Boolean upcoming,
            @RequestParam(required = false) Boolean organizedByClub,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getFilteredAndPaginatedEvents(upcoming, organizedByClub, pageable));
    }
}