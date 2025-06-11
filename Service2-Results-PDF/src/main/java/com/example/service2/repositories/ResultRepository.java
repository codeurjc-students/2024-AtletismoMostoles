package com.example.service2.repositories;

import com.example.service2.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    // Find all results by athlete ID
    List<Result> findByAtletaId(Long atletaId);

    // Find all results by event ID
    List<Result> findByEventoId(Long eventoId);
}
