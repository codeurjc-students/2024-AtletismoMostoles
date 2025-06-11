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

        // Simulate PDF generation by creating a .txt file
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("PDF for " + tipo + " ID: " + id + "\n\n");
            for (Result result : resultados) {
                writer.write(result.toString() + "\n");
            }

            // Save to DB
            PdfRequest pdfRequest = new PdfRequest();
            pdfRequest.setRequestId(requestId);
            pdfRequest.setTimestampGenerado(Instant.now());
            pdfRequest.setUrlBlob(fileUrl);
            pdfRequest.setEstado("GENERADO");

            pdfRequestRepository.save(pdfRequest);

            // Notify service1 via RabbitMQ (queue B)
            confirmationSender.sendConfirmation(pdfRequest);

        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());

            PdfRequest failedRequest = new PdfRequest();
            failedRequest.setRequestId(requestId);
            failedRequest.setTimestampGenerado(Instant.now());
            failedRequest.setUrlBlob(null);
            failedRequest.setEstado("ERROR");

            pdfRequestRepository.save(failedRequest);
        }
    }
}
