package com.example.service3.config;

import com.example.service3.entities.Event;
import com.example.service3.repositories.EventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final EventRepository eventRepository;

    private static final String DEFAULT_IMAGE =
            "https://www.atletismomadrid.com/images/stories/fam.jpg";

    public DataInitializer(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void initData() {
        if (eventRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();  // para usar la misma hora base

            List<Event> eventos = List.of(
                    new Event("Control de Lanzamientos",
                            LocalDate.of(2024, 3, 15),
                            "https://goo.gl/maps/lanzamientos", DEFAULT_IMAGE, false,
                            Set.of(1L, 2L), now.minusDays(10)),

                    new Event("Campeonato Autonómico Sub-20",
                            LocalDate.of(2024, 5, 10),
                            "https://goo.gl/maps/vallehermoso", DEFAULT_IMAGE, true,
                            Set.of(2L, 3L), now.minusDays(8)),

                    new Event("Jornada de Pruebas Combinadas",
                            LocalDate.of(2024, 7, 1),
                            "https://goo.gl/maps/mostoles", DEFAULT_IMAGE, true,
                            Set.of(3L, 4L), now.minusDays(5)),

                    new Event("Torneo Escolar de Velocidad",
                            LocalDate.of(2024, 9, 21),
                            "https://goo.gl/maps/escuela", DEFAULT_IMAGE, false,
                            Set.of(1L, 4L, 5L), now.minusDays(3)),

                    new Event("Cross Popular Villa de Madrid",
                            LocalDate.of(2024, 11, 3),
                            "https://goo.gl/maps/parquemadrid", DEFAULT_IMAGE, true,
                            Set.of(2L, 5L), now.minusDays(1))
            );

            eventRepository.saveAll(eventos);
        }
    }
}
