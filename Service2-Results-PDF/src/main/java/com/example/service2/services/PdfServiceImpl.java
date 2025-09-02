package com.example.service2.services;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.dto.AthleteDto;
import com.example.service2.dto.DisciplineDto;
import com.example.service2.dto.EventDto;
import com.example.service2.dto.ResultExDto;
import com.example.service2.entities.PdfHistory;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfHistoryRepository;
import com.example.service2.repositories.ResultRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private PdfHistoryRepository pdfHistoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ResultRepository resultRepository;

    private ResultExDto enrichResult(Result result) {
        String athleteId = result.getAthleteId();
        Long eventId = result.getEventId();
        Long disciplineId = result.getDisciplineId();
        
        String athleteName = restTemplate.getForObject(
                "http://service1-backend:8081/api/athletes/" + athleteId, AthleteDto.class
        ).getFullName();

        String eventName = restTemplate.getForObject(
                "http://service1-backend:8081/api/events/" + eventId, EventDto.class
        ).getName();

        String disciplineName = restTemplate.getForObject(
                "http://service1-backend:8081/api/disciplines/" + disciplineId, DisciplineDto.class
        ).getName();

        return new ResultExDto(athleteName, eventName, disciplineName, result.getValue());
    }

    @Override
    public void generatePdfForAthlete(String athleteId) {
        List<Result> results = resultRepository.findByAthleteId(athleteId);
        if (results.isEmpty()) {
            System.out.println("No hay resultados para el atleta " + athleteId);
            return;
        }

        List<ResultExDto> extended = results.stream()
                .map(this::enrichResult)
                .filter(Objects::nonNull)
                .toList();

        byte[] pdf = generatePdfFromResults(extended);

        String fileName = "resultados_" + athleteId + "_" + UUID.randomUUID() + ".pdf";
        String url = azureBlobService.uploadPdf(fileName, pdf);

        PdfHistory pdfHistory = new PdfHistory(athleteId, url);
        pdfHistoryRepository.save(pdfHistory);

        Map<String, String> confirmation = new HashMap<>();
        confirmation.put("athleteId", athleteId);
        confirmation.put("url", url);
        rabbitTemplate.convertAndSend(RabbitMQConfig.PDF_CONFIRMATION_QUEUE, confirmation);
    }

    @Override
    public List<String> getUrlsByAthleteId(String athleteId) {
        return pdfHistoryRepository.findByAthleteId(athleteId).stream()
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

    private byte[] generatePdfFromResults(List<ResultExDto> extended) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph title = new Paragraph("Resultados del atleta", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 4f, 4f, 2f});

            Stream.of("Atleta", "Evento", "Disciplina", "Valor").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                header.setBackgroundColor(new Color(144, 238, 144));
                header.setPadding(8f);
                table.addCell(header);
            });

            for (ResultExDto dto : extended) {
                table.addCell(new PdfPCell(new Phrase(dto.getAthleteFullName())));
                table.addCell(new PdfPCell(new Phrase(dto.getEventName())));
                table.addCell(new PdfPCell(new Phrase(dto.getDisciplineName())));
                table.addCell(new PdfPCell(new Phrase(dto.getValue())));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
