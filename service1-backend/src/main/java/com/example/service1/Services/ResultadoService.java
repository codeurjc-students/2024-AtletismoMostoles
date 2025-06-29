package com.example.service1.Services;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service1.GrpcClients.ResultadoGrpcCliente;
import com.example.service2.grpc.ResultadoServiceGrpcProto;
import com.example.service2.grpc.ResultadoServiceGrpcProto.GuardarResultadoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoGrpcCliente grpcClient;

    public List<ResultadoDto> getResultadosDeAtleta(Long atletaId) {
        return grpcClient.verResultados(atletaId);
    }

    public List<ResultadoDto> getResultadosDeEvento(Long eventoId) {
        return grpcClient.verResultadosPorEvento(eventoId);
    }

    public ResultadoDto guardarResultado(Long atletaId, Long eventoId, Long disciplinaId, double marca, LocalDate fecha) {
        return grpcClient.guardarResultado(atletaId, eventoId, disciplinaId, marca, fecha);
    }

    public List<ResultadoDto> guardarResultadosBatchDesdeDto(List<ResultadoDto> dtos) {
        // 1. Convertir a requests
        List<GuardarResultadoRequest> requests = dtos.stream()
                .map(dto -> GuardarResultadoRequest.newBuilder()
                        .setAtletaId(dto.getAtletaId())
                        .setEventoId(dto.getEventoId())
                        .setDisciplinaId(dto.getDisciplinaId())
                        .setMarca(dto.getMarca())
                        .setFecha(dto.getFecha().toString())
                        .build())
                .toList();

        // 2. Llamar al cliente gRPC
        List<ResultadoServiceGrpcProto.ResultadoMessage> respuestas = grpcClient.guardarResultadosBatch(requests);

        // 3. Convertir de vuelta a DTOs
        return respuestas.stream()
                .map(msg -> {
                    ResultadoDto dto = new ResultadoDto();
                    dto.setId(msg.getId());
                    dto.setAtletaId(msg.getAtletaId());
                    dto.setEventoId(msg.getEventoId());
                    dto.setDisciplinaId(msg.getDisciplinaId());
                    dto.setMarca(msg.getMarca());
                    dto.setFecha(LocalDate.parse(msg.getFecha()));
                    return dto;
                })
                .toList();
    }


    public ResultadoDto actualizarResultado(ResultadoDto dto) {
        return grpcClient.actualizarResultado(dto);
    }

    public boolean borrarResultado(Long id) {
        return grpcClient.borrarResultado(id);
    }

    public ResultadoDto getResultadoPorId(Long id) {
        return grpcClient.getResultadoPorId(id);
    }

    public List<ResultadoDto> buscarResultadosFiltrados(Long eventoId, Long disciplinaId) {
        return grpcClient.buscarResultadosFiltrados(eventoId, disciplinaId);
    }

    public List<PdfDto> getHistorialPdf(Long atletaId) {
        return grpcClient.listaPdfHistorico(atletaId);
    }
}
