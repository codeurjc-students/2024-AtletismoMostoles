package com.example.service1.RestControllers;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service1.Services.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultRestController {

    @Autowired
    private ResultService resultService;

    @GetMapping
    public ResponseEntity<Page<ResultadoDto>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultadoDto> allResults = resultService.getAllResultados();
        return ResponseEntity.ok(paginate(allResults, page, size, sortBy));
    }

    @GetMapping("/athlete/{atletaId}")
    public ResponseEntity<Page<ResultadoDto>> getResultsByAthlete(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultadoDto> results = resultService.getResultadosDeAtleta(atletaId);
        return ResponseEntity.ok(paginate(results, page, size, sortBy));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<ResultadoDto>> getResultsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultadoDto> results = resultService.getResultadosDeEvento(eventId);
        return ResponseEntity.ok(paginate(results, page, size, sortBy));
    }

    @GetMapping("/pdf/history/{atletaId}")
    public ResponseEntity<Page<PdfDto>> getHistorialPdf(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestampGenerado") String sortBy
    ) {
        List<PdfDto> pdfs = resultService.getHistorialPdf(atletaId);
        return ResponseEntity.ok(paginate(pdfs, page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoDto> getResultById(@PathVariable Long id) {
        ResultadoDto result = resultService.getResultadoPorId(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ResultadoDto> createResult(@Valid @RequestBody ResultadoDto dto) {
        ResultadoDto saved = resultService.guardarResultado(
                dto.getAtletaId(), dto.getEventoId(), dto.getDisciplinaId(), dto.getValor());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResultadoDto>> createMultipleResults(@RequestBody List<ResultadoDto> DTos) {
        List<ResultadoDto> saved = resultService.guardarResultadosBatchDesdeDto(DTos);
        return ResponseEntity.ok(saved);
    }


    @PostMapping("/pdf/{atletaId}")
    public ResponseEntity<Void> solicitarGeneracionPdf(@PathVariable String atletaId) {
        resultService.solicitarGeneracionPdf(atletaId);
        return ResponseEntity.accepted().build();
    }

    private <T> Page<T> paginate(List<T> items, int page, int size, String sortBy) {
        int start = Math.min(page * size, items.size());
        int end = Math.min(start + size, items.size());
        List<T> content = items.subList(start, end);
        return new PageImpl<>(content, PageRequest.of(page, size, Sort.by(sortBy)), items.size());
    }
}
