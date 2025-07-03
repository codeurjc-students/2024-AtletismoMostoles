package com.example.service1.Services;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service1.GrpcClients.ResultadoGrpcCliente;
import com.example.service1.Sender.PdfRequestSender;
import com.example.service1.DTO.PdfGenerationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    @Autowired
    private ResultadoGrpcCliente grpcClient;

    @Autowired
    private PdfRequestSender pdfRequestSender;

    public List<ResultadoDto> getAllResultados() {
        return grpcClient.getAllResultados();
    }

    public List<ResultadoDto> getResultadosDeAtleta(String atletaId) {
        return grpcClient.getResultadosPorAtleta(atletaId);
    }

    public List<ResultadoDto> getResultadosDeEvento(Long eventoId) {
        return grpcClient.verResultadosPorEvento(eventoId);
    }

    public ResultadoDto guardarResultado(String atletaId, Long eventoId, Long disciplinaId, String valor) {
        return grpcClient.guardarResultado(atletaId, eventoId, disciplinaId, valor);
    }

    public List<ResultadoDto> guardarResultadosBatchDesdeDto(List<ResultadoDto> DTos) {
        return grpcClient.guardarResultadosBatch(DTos);
    }

    public ResultadoDto getResultadoPorId(Long id) {
        return grpcClient.getResultadoPorId(id);
    }

    public List<PdfDto> getHistorialPdf(String atletaId) {
        return grpcClient.listaPdfHistorico(atletaId);
    }

    public void solicitarGeneracionPdf(String atletaId) {
        pdfRequestSender.sendRequest(new PdfGenerationRequest(atletaId));
    }
}
