package com.example.service2.repositories;

import com.example.service2.entities.PdfRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PdfRequestRepository extends JpaRepository<PdfRequest, String> {
    List<PdfRequest> findByAtletaId(Long atletaId);
}