package com.example.service2.services;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.entities.PdfRequest;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfRequestRepository;
import com.example.service2.repositories.ResultRepository;
import com.example.service2.messaging.PdfConfirmationSender;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PdfServiceImpl implements PdfService {

    private final ResultRepository resultRepository;
    private final PdfRequestRepository pdfRequestRepository;
    private final PdfConfirmationSender confirmationSender;

    public PdfServiceImpl(ResultRepository resultRepository,
                          PdfRequestRepository pdfRequestRepository,
                          PdfConfirmationSender confirmationSender) {
        this.resultRepository = resultRepository;
        this.pdfRequestRepository = pdfRequestRepository;
        this.confirmationSender = confirmationSender;
    }

    @Override
    public void generarPdfParaAtleta(Long atletaId) {
        List<Result> resultados = resultRepository.findByAtletaId(atletaId);
        generateAndSavePdf("athlete", atletaId, resultados);
    }

    @Override
    public void generarPdfParaEvento(Long eventoId) {
        List<Result> resultados = resultRepository.findByEventoId(eventoId);
        generateAndSavePdf("event", eventoId, resultados);
    }

    @Override
    public List<PdfRequest> findHistorialPorAtletaId(Long atletaId) {
        return pdfRequestRepository.findByAtletaId(atletaId);
    }

    private void generateAndSavePdf(String tipo, Long id, List<Result> resultados) {
        String requestId = UUID.randomUUID().toString();
        String fileName = "generated_" + tipo + "_" + id + "_" + requestId + ".txt";
        String fileUrl = "/simulated/azure/blob/" + fileName;

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("PDF for " + tipo + " ID: " + id + "\n\n");
            for (Result result : resultados) {
                writer.write("Resultado ID: " + result.getId() + "\n");
                writer.write("Atleta ID: " + result.getAtletaId() + "\n");
                writer.write("Evento ID: " + result.getEventoId() + "\n");
                writer.write("Disciplina ID: " + result.getDisciplinaId() + "\n"); // 🆕
                writer.write("Marca: " + result.getMarca() + "\n");
                writer.write("Fecha: " + result.getFecha() + "\n");
                writer.write("----\n");
            }

            PdfRequest pdfRequest = new PdfRequest();
            pdfRequest.setRequestId(requestId);
            pdfRequest.setTimestampGenerado(Instant.now());
            pdfRequest.setUrlBlob(fileUrl);
            pdfRequest.setEstado("GENERADO");

            if (tipo.equals("athlete")) {
                pdfRequest.setAtletaId(id);
            } else {
                pdfRequest.setEventoId(id);
            }

            pdfRequestRepository.save(pdfRequest);
            confirmationSender.sendConfirmation(pdfRequest);

        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());

            PdfRequest failedRequest = new PdfRequest();
            failedRequest.setRequestId(requestId);
            failedRequest.setTimestampGenerado(Instant.now());
            failedRequest.setUrlBlob(null);
            failedRequest.setEstado("ERROR");

            if (tipo.equals("athlete")) {
                failedRequest.setAtletaId(id);
            } else {
                failedRequest.setEventoId(id);
            }

            pdfRequestRepository.save(failedRequest);
        }
    }

}
