package com.example.service1.RestControllers;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.Services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventDto>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) Boolean organizedByClub,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<EventDto> events = eventService.listEvents();

        Stream<EventDto> filtered = events.stream();
        if (organizedByClub != null)
            filtered = filtered.filter(e -> e.isOrganizedByClub() == organizedByClub);
        if (startDate != null)
            filtered = filtered.filter(e -> !e.getDate().isBefore(startDate));
        if (endDate != null)
            filtered = filtered.filter(e -> !e.getDate().isAfter(endDate));

        List<EventDto> sorted = filtered
                .sorted(Comparator.comparing(e -> {
                    if ("name".equals(sortBy)) return e.getName();
                    if ("date".equals(sortBy)) return e.getDate().toString();
                    return e.getName();
                }))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, sorted.size());
        List<EventDto> paged = start >= sorted.size() ? List.of() : sorted.subList(start, end);

        Page<EventDto> pageResult = new PageImpl<>(paged, PageRequest.of(page, size), sorted.size());
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto dto) {
        return ResponseEntity.ok(eventService.createEvent(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventDto dto) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notificaciones")
    public List<EventNotificationDto> getNotifications(@RequestParam long userId, @RequestParam String lastConnection) {
        return eventService.obtenerNotificaciones(userId, lastConnection);
    }
}