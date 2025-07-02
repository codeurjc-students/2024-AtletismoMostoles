package com.example.service2.services;

import com.example.service2.dto.PdfConfirmationMessage;
import com.example.service2.entities.PdfHistory;
import com.example.service2.repositories.PdfHistoryRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private PdfHistoryRepository pdfHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<String> getUrlsByAthleteId(Long athleteId) {
        return pdfHistoryRepository.findByAthleteId(athleteId)
                .stream()
                .map(PdfHistory::getUrl)
                .collect(Collectors.toList());
    }

    @Override
    public void generarPdfParaAtleta(Long athleteId) {
        String url = "https://fake.storage.net/pdfs/atleta-" + athleteId + "-" + Instant.now().toEpochMilli() + ".pdf";

        PdfHistory pdf = new PdfHistory(athleteId, url);
        pdfHistoryRepository.save(pdf);

        PdfConfirmationMessage message = new PdfConfirmationMessage(athleteId, url);
        rabbitTemplate.convertAndSend("cola.B", message);
    }

    @Override
    public void saveUrlForAthlete(Long athleteId, String url) {
        PdfHistory entry = new PdfHistory();
        entry.setAthleteId(athleteId);
        entry.setUrl(url);
        pdfHistoryRepository.save(entry);
    }
}
