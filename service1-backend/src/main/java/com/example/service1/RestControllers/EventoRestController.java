package com.example.service1.RestControllers;

import com.example.service1.DTO.EventDto;
import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.Services.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/events")
public class EventoRestController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<Page<EventDto>> getEventos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) Boolean organizedByClub,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<EventDto> eventos = eventoService.listarEventos();

        Stream<EventDto> filtered = eventos.stream();
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
    public ResponseEntity<EventDto> getEvento(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }

    @PostMapping
    public ResponseEntity<EventDto> crearEvento(@Valid @RequestBody EventDto dto) {
        return ResponseEntity.ok(eventoService.crearEvento(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> actualizarEvento(@PathVariable Long id, @RequestBody EventDto dto) {
        return ResponseEntity.ok(eventoService.actualizarEvento(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarEvento(@PathVariable Long id) {
        eventoService.borrarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notificaciones")
    public List<EventNotificationDto> getNotificaciones(@RequestParam long usuarioId, @RequestParam String ultimaConexion) {
        return eventoService.obtenerNotificaciones(usuarioId, ultimaConexion);
    }
}