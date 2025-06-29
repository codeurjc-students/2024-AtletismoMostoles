package com.example.service2.config;

import com.example.service2.entities.PdfRequest;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfRequestRepository;
import com.example.service2.repositories.ResultRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer {

    private final ResultRepository resultRepository;
    private final PdfRequestRepository pdfRequestRepository;

    public DataInitializer(ResultRepository resultRepository, PdfRequestRepository pdfRequestRepository) {
        this.resultRepository = resultRepository;
        this.pdfRequestRepository = pdfRequestRepository;
    }

    @PostConstruct
    public void initData() {
        if (resultRepository.count() == 0) {
            // Crear resultados
            List<Result> results = List.of(
                    createResult(1L, 1L, 12.45, LocalDate.of(2024, 4, 1)),
                    createResult(1L, 1L, 12.60, LocalDate.of(2024, 5, 2)),
                    createResult(2L, 3L, 11.85, LocalDate.of(2024, 4, 1)),
                    createResult(3L, 2L, 13.20, LocalDate.of(2024, 6, 10)),
                    createResult(2L, 3L, 12.10, LocalDate.of(2024, 5, 2)),
                    createResult(3L, 2L, 12.45, LocalDate.of(2025, 4, 1)),
                    createResult(4L, 2L, 12.60, LocalDate.of(2025, 5, 20)),
                    createResult(4L, 4L, 11.85, LocalDate.of(2025, 4, 15)),
                    createResult(4L, 4L, 13.20, LocalDate.of(2025, 8, 10)),
                    createResult(2L, 5L, 12.10, LocalDate.of(2025, 3, 2))
            );
            resultRepository.saveAll(results);
        }

        if (pdfRequestRepository.count() == 0) {
            // Crear historial de PDF simulados
            List<PdfRequest> pdfs = List.of(
                    createPdf("GENERADO", 1L, null, "https://fake.url/pdf/atleta1-2024.pdf"),
                    createPdf("PENDIENTE", 2L, null, null),
                    createPdf("ERROR", null, 11L, null)
            );
            pdfRequestRepository.saveAll(pdfs);
        }
    }

    private Result createResult(Long atletaId, Long eventoId, double marca, LocalDate fecha) {
        Result r = new Result();
        r.setAtletaId(atletaId);
        r.setEventoId(eventoId);
        r.setMarca(marca);
        r.setFecha(fecha);
        return r;
    }

    private PdfRequest createPdf(String estado, Long atletaId, Long eventoId, String url) {
        PdfRequest p = new PdfRequest();
        p.setRequestId(UUID.randomUUID().toString());
        p.setAtletaId(atletaId);
        p.setEventoId(eventoId);
        p.setEstado(estado);
        p.setTimestampGenerado(Instant.now());
        p.setUrlBlob(url);
        return p;
    }
}
