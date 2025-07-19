package com.example.service2.services;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.dto.AthleteDto;
import com.example.service2.dto.DisciplineDto;
import com.example.service2.dto.EventDto;
import com.example.service2.dto.ResultadoExDto;
import com.example.service2.entities.PdfHistory;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfHistoryRepository;
import com.example.service2.repositories.ResultRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

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

    private ResultadoExDto enriquecerResultado(Result result) {
        String atletaId = result.getAthleteId();
        Long eventoId = result.getEventId();
        Long disciplinaId = result.getDisciplineId();

        String athleteName = restTemplate.getForObject(
                "https://localhost:443/api/athletes/" + atletaId, AthleteDto.class
        ).getFullName(); // ← combínalo si hace falta

        String eventName= restTemplate.getForObject(
                "https://localhost:443/api/events/" + eventoId, EventDto.class
        ).getName();

        String disciplineName = restTemplate.getForObject(
                "https://localhost:443/api/disciplines/" + disciplinaId, DisciplineDto.class
        ).getName();

        return new ResultadoExDto(athleteName, eventName, disciplineName, result.getValue());
    }

    @Override
    public void generarPdfParaAtleta(String atletaId) {
        List<Result> results = resultRepository.findByAthleteId(atletaId);
        if (results.isEmpty()) {
            System.out.println("No hay resultados para el atleta " + atletaId);
            return;
        }

        List<ResultadoExDto> extended = results.stream()
                .map(this::enriquecerResultado)
                .filter(Objects::nonNull)
                .toList();

        String content= generateContentText(extended);
        byte[] pdf = generarPdfDesdeTexto(content);

        String fileName = "resultados_" + atletaId + "_" + UUID.randomUUID() + ".pdf";
        String url = azureBlobService.uploadPdf(fileName, pdf);

        PdfHistory pdfHistory = new PdfHistory(atletaId, url);
        pdfHistoryRepository.save(pdfHistory);

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

    private String generateContentText(List<ResultadoExDto> extendidos) {
        StringBuilder sb = new StringBuilder();

        sb.append("Resultados del atleta\n\n");
        sb.append(String.format("%-25s %-20s %-20s %-10s\n",
                "Atleta", "Evento", "Disciplina", "Valor"));
        sb.append("=".repeat(80)).append("\n");

        for (ResultadoExDto dto : extendidos) {
            sb.append(String.format("%-25s %-20s %-20s %-10s\n",
                    dto.getAthleteFullName(),
                    dto.getEventName(),
                    dto.getDisciplineName(),
                    dto.getValue()));
        }
        return sb.toString();
    }

    private byte[] generarPdfDesdeTexto(String contenido) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph(contenido));
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

}
