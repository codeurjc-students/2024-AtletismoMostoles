package com.example.service2.services;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.dto.PdfConfirmationMessage;
import com.example.service2.entities.PdfHistory;
import com.example.service2.repositories.PdfHistoryRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private PdfHistoryRepository pdfHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void generarPdfParaAtleta(String atletaId) {
        String url = "https://fake.storage.net/pdfs/" + atletaId + "-" + Instant.now().toEpochMilli() + ".pdf";

        PdfHistory pdf = new PdfHistory(atletaId, url);
        pdfHistoryRepository.save(pdf);

        Map<String, String> confirmation = new HashMap<>();
        confirmation.put("atletaId", atletaId);
        confirmation.put("url", url);

        rabbitTemplate.convertAndSend(RabbitMQConfig.PDF_CONFIRMATION_QUEUE, confirmation);
    }

    @Override
    public List<String> getUrlsByAthleteId(String atletaId) {
        return pdfHistoryRepository.findByAthleteId(atletaId).stream()
                .map(PdfHistory::getUrl)
                .collect(Collectors.toList());
    }

    @Override
    public void saveUrlForAthlete(String athleteId, String url) {
        PdfHistory entry = new PdfHistory();
        entry.setAthleteId(athleteId);
        entry.setUrl(url);
        pdfHistoryRepository.save(entry);
    }
}
