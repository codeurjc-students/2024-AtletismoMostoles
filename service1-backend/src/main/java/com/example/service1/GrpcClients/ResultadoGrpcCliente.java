package com.example.service1.GrpcClients;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service2.grpc.ResultadoServiceGrpc.ResultadoServiceBlockingStub;
import com.example.service2.grpc.ResultadoServiceGrpcProto.*;
import com.example.service3.grpc.EventoServiceGrpcProto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResultadoGrpcCliente {

    private final ResultadoServiceBlockingStub resultadoStub;

    public ResultadoGrpcCliente(ResultadoServiceBlockingStub resultadoStub) {
        this.resultadoStub = resultadoStub;
    }

    public List<ResultadoDto> verResultados(Long atletaId) {
        VerResultadosRequest request = VerResultadosRequest.newBuilder()
                .setAtletaId(atletaId)
                .build();

        VerResultadosResponse response = resultadoStub.verResultados(request);

        return response.getResultadosList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ResultadoDto> verResultadosPorEvento(Long eventoId) {
        VerResultadosPorEventoRequest request = VerResultadosPorEventoRequest.newBuilder()
                .setEventoId(eventoId)
                .build();

        VerResultadosResponse response = resultadoStub.verResultadosPorEvento(request);

        return response.getResultadosList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ResultadoDto guardarResultado(Long atletaId, Long eventoId, Long disciplinaId, double marca, LocalDate fecha) {
        GuardarResultadoRequest grpcReq = GuardarResultadoRequest.newBuilder()
                .setAtletaId(atletaId)
                .setEventoId(eventoId)
                .setDisciplinaId(disciplinaId)
                .setMarca(marca)
                .setFecha(fecha.toString())
                .build();

        ResultadoMessage rpcResp = resultadoStub.guardarResultado(grpcReq);

        return mapToDto(rpcResp);
    }

    public ResultadoDto getResultadoPorId(Long id) {
        GetResultadoRequest request = GetResultadoRequest.newBuilder().setId(id).build();
        ResultadoMessage message = resultadoStub.verResultadoPorId(request);
        return mapToDto(message);
    }

    public List<ResultadoDto> buscarResultadosFiltrados(Long eventId, Long disciplineId) {
        ResultadoFiltroRequest request = ResultadoFiltroRequest.newBuilder()
                .setEventId(eventId)
                .setDisciplineId(disciplineId)
                .build();
        VerResultadosResponse response = resultadoStub.buscarResultadosFiltrados(request);
        return response.getResultadosList().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<ResultadoMessage> guardarResultadosBatch(List<GuardarResultadoRequest> requests) {
        BatchGuardarRequest batch = BatchGuardarRequest.newBuilder()
                .addAllResultados(requests)
                .build();

        BatchGuardarResponse response = resultadoStub.guardarResultadosBatch(batch);
        return response.getResultadosList();
    }


    public ResultadoDto actualizarResultado(ResultadoDto dto) {
        UpdateResultadoRequest request = UpdateResultadoRequest.newBuilder()
                .setId(dto.getId())
                .setAtletaId(dto.getAtletaId())
                .setEventoId(dto.getEventoId())
                .setDisciplinaId(dto.getDisciplinaId())
                .setMarca(dto.getMarca())
                .setFecha(dto.getFecha().toString())
                .build();
        ResultadoMessage response = resultadoStub.actualizarResultado(request);
        return mapToDto(response);
    }

    public boolean borrarResultado(Long id) {
        DeleteResultadoRequest request = DeleteResultadoRequest.newBuilder().setId(id).build();
        EventoServiceGrpcProto.StatusMessage status = resultadoStub.borrarResultado(request);
        return status.getSuccess();
    }

    public List<PdfDto> listaPdfHistorico(Long atletaId) {
        ListaPdfRequest request = ListaPdfRequest.newBuilder()
                .setAtletaId(atletaId)
                .build();

        ListaPdfResponse response = resultadoStub.listaPdfHistorico(request);

        return response.getPdfsList().stream()
                .map(p -> {
                    PdfDto dto = new PdfDto();
                    dto.setRequestId(p.getRequestId());
                    dto.setAtletaId(p.getAtletaId());
                    dto.setEventoId(p.getEventoId());
                    dto.setTimestampGenerado(Instant.parse(p.getTimestampGenerado()));
                    dto.setUrlBlob(p.getUrlBlob());
                    dto.setEstado(p.getEstado());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ========= Map helpers =========

    private ResultadoDto mapToDto(ResultadoData data) {
        ResultadoDto dto = new ResultadoDto();
        dto.setId(data.getId());
        dto.setAtletaId(data.getAtletaId());
        dto.setEventoId(data.getEventoId());
        dto.setDisciplinaId(data.getDisciplinaId());
        dto.setMarca(data.getMarca());
        dto.setFecha(LocalDate.parse(data.getFecha()));
        return dto;
    }

    private ResultadoDto mapToDto(ResultadoMessage msg) {
        ResultadoDto dto = new ResultadoDto();
        dto.setId(msg.getId());
        dto.setAtletaId(msg.getAtletaId());
        dto.setEventoId(msg.getEventoId());
        dto.setDisciplinaId(msg.getDisciplinaId());
        dto.setMarca(msg.getMarca());
        dto.setFecha(LocalDate.parse(msg.getFecha()));
        return dto;
    }
}
