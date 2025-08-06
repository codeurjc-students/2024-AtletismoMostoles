package com.example.service1.Services;

import com.example.service1.Configuration.RabbitMQConfig;
import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultDto;
import com.example.service1.GrpcClients.ResultGrpcClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    @Autowired
    private ResultGrpcClient grpcClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<ResultDto> getAllResults() {
        return grpcClient.getAllResults();
    }

    public List<ResultDto> getResultsByAthlete(String athleteId) {
        return grpcClient.getResultsByAthlete(athleteId);
    }

    public List<ResultDto> getResultsByEvent(Long eventId) {
        return grpcClient.getResultsByEvent(eventId);
    }

    public ResultDto saveResult(String athleteId, Long eventId, Long disciplineId, String value) {
        return grpcClient.saveResult(athleteId, eventId, disciplineId, value);
    }

    public List<ResultDto> saveResultsBatchFromDto(List<ResultDto> DTos) {
        return grpcClient.saveResultsBatch(DTos);
    }

    public ResultDto getResultById(Long id) {
        return grpcClient.getResultById(id);
    }

    public List<PdfDto> getHistorialPdf(String athleteId) {
        return grpcClient.listPdfHistorical(athleteId);
    }

    public void requestGenerationPdf(String athleteId) {
        Map<String, String> message = new HashMap<>();
        message.put("atletaId", athleteId);

        rabbitTemplate.convertAndSend(RabbitMQConfig.PDF_REQUEST_QUEUE, message);
    }
}
