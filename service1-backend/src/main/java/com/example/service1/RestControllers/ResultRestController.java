package com.example.service1.RestControllers;

import com.example.service1.DTO.ResultadoDto;
import com.example.service1.DTO.PdfDto;
import com.example.service1.Services.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "https://localhost:4200")
public class ResultRestController {

    @Autowired
    private ResultadoService resultService;

    @GetMapping
    public ResponseEntity<Page<ResultadoDto>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long disciplineId) {

        List<ResultadoDto> resultados;

        if (eventId != null && disciplineId != null) {
            resultados = resultService.buscarResultadosFiltrados(eventId, disciplineId);
        } else if (eventId != null) {
            resultados = resultService.getResultadosDeEvento(eventId);
        } else {
            resultados = resultService.getResultadosDeAtleta(0L);
        }

        int start = Math.min(page * size, resultados.size());
        int end = Math.min((page + 1) * size, resultados.size());
        List<ResultadoDto> pageContent = resultados.subList(start, end);
        Page<ResultadoDto> resultPage = new PageImpl<>(pageContent, PageRequest.of(page, size, Sort.by(sortBy)), resultados.size());

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoDto> getResult(@PathVariable Long id) {
        ResultadoDto result = resultService.getResultadoPorId(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ResultadoDto> createResult(@RequestBody ResultadoDto dto) {
        ResultadoDto saved = resultService.guardarResultado(
                dto.getAtletaId(), dto.getEventoId(), dto.getDisciplinaId(), dto.getMarca(), dto.getFecha());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultadoDto> updateResult(@PathVariable Long id, @RequestBody ResultadoDto dto) {
        dto.setId(id);
        ResultadoDto updated = resultService.actualizarResultado(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        boolean success = resultService.borrarResultado(id);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResultadoDto>> createMultipleResults(@RequestBody List<ResultadoDto> lista) {
        List<ResultadoDto> saved = resultService.guardarResultadosBatchDesdeDto(lista);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/pdfs/{atletaId}")
    public ResponseEntity<List<PdfDto>> getPdfHistorial(@PathVariable Long atletaId) {
        return ResponseEntity.ok(resultService.getHistorialPdf(atletaId));
    }
}
