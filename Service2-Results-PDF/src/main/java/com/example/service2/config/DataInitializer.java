package com.example.service2.config;

import com.example.service2.entities.PdfHistory;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfHistoryRepository;
import com.example.service2.repositories.ResultRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {

    private final ResultRepository resultsRepository;
    private final PdfHistoryRepository pdfHistoryRepository;

    public DataInitializer(ResultRepository resultsRepository, PdfHistoryRepository pdfHistoryRepository) {
        this.resultsRepository = resultsRepository;
        this.pdfHistoryRepository = pdfHistoryRepository;
    }

    @PostConstruct
    public void initData() {
        if (resultsRepository.count() == 0) {
            List<Result> results = List.of(
                    createResult(1L, 1L, 1L, "12.45"),
                    createResult(1L, 1L, 1L, "12.60"),
                    createResult(3L, 2L, 2L, "11.85"),
                    createResult(2L, 3L, 3L, "13.20"),
                    createResult(2L, 3L, 3L, "12.10"),
                    createResult(1L, 4L, 2L, "12.45"),
                    createResult(4L, 2L, 3L, "12.60"),
                    createResult(2L, 1L, 4L, "11.85"),
                    createResult(3L, 3L, 4L, "13.20"),
                    createResult(4L, 1L, 5L, "12.10")
            );
            resultsRepository.saveAll(results);
        }

        if (pdfHistoryRepository.count() == 0) {
            List<PdfHistory> pdfs = List.of(
                    new PdfHistory(1L, "https://fake.url/pdf/atleta1-2024.pdf"),
                    new PdfHistory(2L, "https://fake.url/pdf/atleta2-2024.pdf"),
                    new PdfHistory(3L, "https://fake.url/pdf/atleta3-2024.pdf")
            );
            pdfHistoryRepository.saveAll(pdfs);
        }
    }

    private Result createResult(Long athleteId, Long eventId, Long disciplineId, String value) {
        return new Result(null, eventId, disciplineId, athleteId, value);
    }
}
